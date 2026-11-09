package br.pucminas.moeda;

import br.pucminas.moeda.domain.*;
import br.pucminas.moeda.repository.*;
import br.pucminas.moeda.service.*;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.net.ServerSocket;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "app.mail.enabled=true",
        "spring.datasource.url=${TEST_DATABASE_URL:jdbc:h2:mem:smtp;DB_CLOSE_DELAY=-1}",
        "spring.mail.default-encoding=UTF-8",
        "spring.mail.properties.mail.smtp.connectiontimeout=500",
        "spring.mail.properties.mail.smtp.timeout=500",
        "spring.mail.properties.mail.smtp.writetimeout=500"
})
class EmailSmtpTest {
    private static final GreenMail smtp = new GreenMail(ServerSetup.SMTP.dynamicPort());
    static { smtp.start(); }

    @DynamicPropertySource
    static void smtpProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.mail.host", () -> "127.0.0.1");
        properties.add("spring.mail.port", () -> smtp.getSmtp().getPort());
    }

    @Autowired EmailService emailService;
    @Autowired EmailDispatcher dispatcher;
    @Autowired NotificacaoEmailRepository emails;
    @Autowired JavaMailSenderImpl sender;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired AlunoRepository alunos;
    @Autowired ProfessorRepository professores;
    @Autowired EmpresaParceiraRepository empresas;
    @Autowired InstituicaoEnsinoRepository instituicoes;
    @Autowired VantagemRepository vantagens;
    @Autowired ProfessorService professorService;
    @Autowired ResgateService resgateService;

    @BeforeEach void limparCaixa() throws Exception {
        smtp.purgeEmailFromAllMailboxes();
        emails.deleteAll();
        sender.setPort(smtp.getSmtp().getPort());
    }

    @AfterAll static void encerrar() { smtp.stop(); }

    @Test void moedasECupomSaoEntreguesPorSmtpAosDestinatariosCertos() throws Exception {
        var inst = instituicoes.findAll().get(0);
        String uid = UUID.randomUUID().toString().substring(0, 12);
        var aluno = alunos.save(new Aluno("Aluno", uid+"@aluno.test", "senha", uid, "RG", "Rua", "Curso", inst));
        var professor = new Professor("Professor", uid+"@prof.test", "senha", uid, "Depto", inst);
        professor.setSaldoMoedas(200);
        professor = professores.save(professor);
        var empresa = empresas.save(new EmpresaParceira("Cantina", uid+"@empresa.test", "senha", null));
        var vantagem = new Vantagem();
        vantagem.setDescricao("Almoço"); vantagem.setCustoEmMoedas(80); vantagem.setFotoUrl("/foto.png"); vantagem.setEmpresa(empresa);
        vantagem = vantagens.save(vantagem);

        professorService.enviarMoedas(professor.getId(), aluno.getId(), 100, "Participação em aula");
        var resgate = resgateService.resgatar(aluno.getId(), vantagem.getId());
        assertTrue(smtp.waitForIncomingEmail(5000, 3));
        assertEquals(3, smtp.getReceivedMessages().length);
        int cupons = 0;
        for (var mensagem : smtp.getReceivedMessages()) {
            String para = mensagem.getAllRecipients()[0].toString();
            String corpo = mensagem.getContent().toString();
            if (mensagem.getSubject().contains("recebeu moedas")) {
                assertEquals(aluno.getEmail(), para);
                assertTrue(corpo.contains("Participação em aula"));
                assertTrue(corpo.contains("100 moedas"));
            } else {
                assertTrue(para.equals(aluno.getEmail()) || para.equals(empresa.getEmail()));
                assertTrue(corpo.contains(resgate.getCodigoCupom()));
                cupons++;
            }
        }
        assertEquals(2, cupons);
        assertEquals(2, emails.findByDestinatarioOrderByDataEnvioDesc(aluno.getEmail()).size());
        assertEquals(1, emails.findByDestinatarioOrderByDataEnvioDesc(empresa.getEmail()).size());
        assertTrue(emails.findAll().stream().allMatch(e -> e.getStatus() == NotificacaoEmail.Status.ENVIADO && e.getEnviadoEm() != null));
        assertEquals(20, alunos.findById(aluno.getId()).orElseThrow().getSaldoMoedas());
        dispatcher.reenviarPendentes();
        assertEquals(3, smtp.getReceivedMessages().length, "Mensagens confirmadas não são reenviadas");
    }

    @Test void rollbackNaoEnviaNemDeixaNotificacaoNaFila() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            emailService.enviar("rollback@test.local", "Operação cancelada", "Não enviar");
            assertEquals(0, smtp.getReceivedMessages().length, "SMTP só pode ocorrer após commit");
            status.setRollbackOnly();
        });
        assertEquals(0, emails.count());
        assertEquals(0, smtp.getReceivedMessages().length);
    }

    @Test void falhaSmtpMantemOperacaoEReenviaComOMesmoCupom() throws Exception {
        int indisponivel;
        try (var porta = new ServerSocket(0)) { indisponivel = porta.getLocalPort(); }
        sender.setPort(indisponivel);
        var email = emailService.enviar("retry@test.local", "Cupom", "Código do cupom: ABCD2345");
        var falha = emails.findById(email.getId()).orElseThrow();
        assertEquals(NotificacaoEmail.Status.FALHA, falha.getStatus());
        assertNull(falha.getEnviadoEm());
        assertEquals(1, falha.getTentativas());
        sender.setPort(smtp.getSmtp().getPort());
        dispatcher.reenviarPendentes();
        assertTrue(smtp.waitForIncomingEmail(5000, 1));
        assertTrue(smtp.getReceivedMessages()[0].getContent().toString().contains("ABCD2345"));
        var enviado = emails.findById(email.getId()).orElseThrow();
        assertEquals(NotificacaoEmail.Status.ENVIADO, enviado.getStatus());
        assertEquals(2, enviado.getTentativas());
    }

    @Test void falhasParamNoLimiteDeTentativas() throws Exception {
        int indisponivel;
        try (var porta = new ServerSocket(0)) { indisponivel = porta.getLocalPort(); }
        sender.setPort(indisponivel);
        var email = emailService.enviar("limite@test.local", "Cupom", "Código: XYZ");
        for (int i = 0; i < 6; i++) dispatcher.reenviarPendentes();
        assertEquals(5, emails.findById(email.getId()).orElseThrow().getTentativas());
        assertEquals(NotificacaoEmail.Status.FALHA, emails.findById(email.getId()).orElseThrow().getStatus());
    }
}
