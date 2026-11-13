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
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        mockMvc.perform(get("/api/instituicoes;jsessionid=abc"))
                .andExpect(status().isOk());
    }

    @Test
    void visitanteEhEnviadoAoLogin() throws Exception {
        mockMvc.perform(get("/api/professor"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Entre para continuar."));
    }

    @Test
    void fluxoDeMoedasVantagemECupom() throws Exception {
        mockMvc.perform(post("/api/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","senha":"errada"}
                                """.formatted(DataLoader.PROFESSOR_EMAIL)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos"));

        Long instituicaoId = instituicoes.findByNome("PUC Minas").orElseThrow().getId();
        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Maria Silva","email":"maria@aluno.com","senha":"123456","cpf":"333.333.333-33","rg":"MG123456","endereco":"Rua A, 10","curso":"Engenharia de Software","instituicaoId":%d}
                                """.formatted(instituicaoId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Cantina Central","email":"cantina@empresa.com","senha":"123456"}
                                """))
                .andExpect(status().isCreated());

        MockHttpSession professor = login(DataLoader.PROFESSOR_EMAIL, DataLoader.PROFESSOR_SENHA, "PROFESSOR");
        mockMvc.perform(get("/api/professor").session(professor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoMoedas").value(1000));

        mockMvc.perform(get("/api/professor").session(professor))
                .andExpect(jsonPath("$.saldoMoedas").value(1000));

        Usuario maria = usuarios.findByEmail("maria@aluno.com").orElseThrow();
        mockMvc.perform(post("/api/professor/envios").session(professor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":%d,"valor":200,"motivo":"Participacao destacada"}
                                """.formatted(maria.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/professor/extrato").session(professor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoMoedas").value(800))
                .andExpect(jsonPath("$.envios[0].motivo").value("Participacao destacada"));

        Professor ana = (Professor) usuarios.findByEmail(DataLoader.PROFESSOR_EMAIL).orElseThrow();
        assertEquals(PeriodoLetivo.atual(), ana.getUltimoPeriodoCreditado());
        assertEquals(800, ana.getSaldoMoedas());

        MockHttpSession aluno = login("maria@aluno.com", "123456", "ALUNO");
        mockMvc.perform(get("/api/aluno/extrato").session(aluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoMoedas").value(200))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Participacao destacada")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("você recebeu moedas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("200 moedas")));

        MockHttpSession empresa = login("cantina@empresa.com", "123456", "EMPRESA");
        MockMultipartFile foto = new MockMultipartFile(
                "foto", "almoco.png", "image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
        mockMvc.perform(multipart("/api/empresa/vantagens").file(foto)
                        .param("descricao", "Desconto no almoco")
                        .param("custoEmMoedas", "150")
                        .session(empresa))
                .andExpect(status().isCreated());

        Long vantagemId = vantagens.findAll().get(0).getId();
        mockMvc.perform(post("/api/aluno/vantagens/" + vantagemId + "/resgatar").session(aluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoCupom").exists());

        MvcResult extrato = mockMvc.perform(get("/api/aluno/extrato").session(aluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoMoedas").value(50))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Desconto no almoco")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Código do cupom:")))
                .andReturn();

        String codigoAluno = codigoDe(extrato.getResponse().getContentAsString());
        mockMvc.perform(get("/api/empresa").session(empresa))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(codigoAluno)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("conferência")));

        List<NotificacaoEmail> doAluno = emails.findByDestinatarioOrderByDataEnvioDesc("maria@aluno.com");
        List<NotificacaoEmail> daEmpresa = emails.findByDestinatarioOrderByDataEnvioDesc("cantina@empresa.com");
        assertTrue(doAluno.stream().anyMatch(e -> e.getCorpo().contains("Código do cupom: " + codigoAluno)));
        assertTrue(daEmpresa.stream().anyMatch(e -> e.getCorpo().contains("Código do cupom: " + codigoAluno)));
        assertEquals(1, daEmpresa.size());

        mockMvc.perform(post("/api/aluno/vantagens/" + vantagemId + "/resgatar").session(aluno))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.containsString("Saldo insuficiente")));

        Aluno saldo = (Aluno) usuarios.findByEmail("maria@aluno.com").orElseThrow();
        assertEquals(50, saldo.getSaldoMoedas());
        assertEquals(1, emails.findByDestinatarioOrderByDataEnvioDesc("cantina@empresa.com").size());
    }

    private MockHttpSession login(String email, String senha, String papel) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","senha":"%s"}
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.papel").value(papel))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private String codigoDe(String json) {
        Matcher matcher = Pattern.compile("Código do cupom: ([A-Z2-9]{8})").matcher(json);
        assertTrue(matcher.find());
        return matcher.group(1);
    }
}
