package br.pucminas.moeda;

import br.pucminas.moeda.config.DataLoader;
import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.NotificacaoEmail;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.domain.Usuario;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import br.pucminas.moeda.repository.NotificacaoEmailRepository;
import br.pucminas.moeda.repository.UsuarioRepository;
import br.pucminas.moeda.repository.VantagemRepository;
import br.pucminas.moeda.service.PeriodoLetivo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FluxoSistemaTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private InstituicaoEnsinoRepository instituicoes;
    @Autowired
    private UsuarioRepository usuarios;
    @Autowired
    private VantagemRepository vantagens;
    @Autowired
    private NotificacaoEmailRepository emails;

    @Test
    void cadastroPublicoIgnoraJsessionNaUrl() throws Exception {
        mockMvc.perform(get("/alunos/novo;jsessionid=abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nome")));
    }

    @Test
    void visitanteEhEnviadoAoLogin() throws Exception {
        mockMvc.perform(get("/professor"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void fluxoDeMoedasVantagemECupom() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", DataLoader.PROFESSOR_EMAIL)
                        .param("senha", "errada"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("E-mail ou senha inválidos")));

        Long instituicaoId = instituicoes.findByNome("PUC Minas").orElseThrow().getId();
        mockMvc.perform(post("/alunos/salvar")
                        .param("nome", "Maria Silva")
                        .param("email", "maria@aluno.com")
                        .param("senha", "123456")
                        .param("cpf", "333.333.333-33")
                        .param("rg", "MG123456")
                        .param("endereco", "Rua A, 10")
                        .param("curso", "Engenharia de Software")
                        .param("instituicaoId", instituicaoId.toString()))
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/empresas/salvar")
                        .param("nome", "Cantina Central")
                        .param("email", "cantina@empresa.com")
                        .param("senha", "123456"))
                .andExpect(redirectedUrl("/login"));

        MockHttpSession professor = login(DataLoader.PROFESSOR_EMAIL, DataLoader.PROFESSOR_SENHA, "/professor");
        mockMvc.perform(get("/professor").session(professor))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1000 moedas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1.000 moedas")));

        mockMvc.perform(get("/professor").session(professor))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1000 moedas")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("2000"))));

        Usuario maria = usuarios.findByEmail("maria@aluno.com").orElseThrow();
        mockMvc.perform(post("/professor/enviar").session(professor)
                        .param("alunoId", maria.getId().toString())
                        .param("valor", "200")
                        .param("motivo", "Participacao destacada"))
                .andExpect(redirectedUrl("/professor/extrato"));

        mockMvc.perform(get("/professor/extrato").session(professor))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Participacao destacada")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("800 moedas")));

        Professor ana = (Professor) usuarios.findByEmail(DataLoader.PROFESSOR_EMAIL).orElseThrow();
        assertEquals(PeriodoLetivo.atual(), ana.getUltimoPeriodoCreditado());
        assertEquals(800, ana.getSaldoMoedas());

        MockHttpSession aluno = login("maria@aluno.com", "123456", "/aluno");
        mockMvc.perform(get("/aluno/extrato").session(aluno))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Participacao destacada")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("você recebeu moedas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("200 moedas")));

        MockHttpSession empresa = login("cantina@empresa.com", "123456", "/empresa");
        MockMultipartFile foto = new MockMultipartFile(
                "foto", "almoco.png", "image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
        mockMvc.perform(multipart("/empresa/vantagens/salvar").file(foto)
                        .param("descricao", "Desconto no almoco")
                        .param("custoEmMoedas", "150")
                        .session(empresa))
                .andExpect(redirectedUrl("/empresa"));

        Long vantagemId = vantagens.findAll().get(0).getId();
        mockMvc.perform(post("/aluno/vantagens/" + vantagemId + "/resgatar").session(aluno))
                .andExpect(redirectedUrl("/aluno/extrato"));

        MvcResult extrato = mockMvc.perform(get("/aluno/extrato").session(aluno))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("50 moedas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Desconto no almoco")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Código do cupom:")))
                .andReturn();

        String codigoAluno = codigoDe(extrato.getResponse().getContentAsString());
        mockMvc.perform(get("/empresa").session(empresa))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(codigoAluno)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("conferência")));

        List<NotificacaoEmail> doAluno = emails.findByDestinatarioOrderByDataEnvioDesc("maria@aluno.com");
        List<NotificacaoEmail> daEmpresa = emails.findByDestinatarioOrderByDataEnvioDesc("cantina@empresa.com");
        assertTrue(doAluno.stream().anyMatch(e -> e.getCorpo().contains("Código do cupom: " + codigoAluno)));
        assertTrue(daEmpresa.stream().anyMatch(e -> e.getCorpo().contains("Código do cupom: " + codigoAluno)));
        assertEquals(1, daEmpresa.size());

        mockMvc.perform(post("/aluno/vantagens/" + vantagemId + "/resgatar").session(aluno))
                .andExpect(redirectedUrl("/aluno/vantagens"));
        mockMvc.perform(get("/aluno/vantagens").session(aluno))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Saldo insuficiente")));

        Aluno saldo = (Aluno) usuarios.findByEmail("maria@aluno.com").orElseThrow();
        assertEquals(50, saldo.getSaldoMoedas());
        assertEquals(1, emails.findByDestinatarioOrderByDataEnvioDesc("cantina@empresa.com").size());
    }

    private MockHttpSession login(String email, String senha, String destino) throws Exception {
        MvcResult result = mockMvc.perform(post("/login").param("email", email).param("senha", senha))
                .andExpect(redirectedUrl(destino))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession();
    }

    private String codigoDe(String html) {
        Matcher matcher = Pattern.compile("Código do cupom: ([A-Z2-9]{8})").matcher(html);
        assertTrue(matcher.find());
        return matcher.group(1);
    }
}
