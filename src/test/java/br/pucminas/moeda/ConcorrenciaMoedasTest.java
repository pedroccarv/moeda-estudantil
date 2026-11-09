package br.pucminas.moeda;

import br.pucminas.moeda.domain.*;
import br.pucminas.moeda.repository.*;
import br.pucminas.moeda.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=${TEST_DATABASE_URL:jdbc:h2:mem:concorrencia;DB_CLOSE_DELAY=-1}")
class ConcorrenciaMoedasTest {
    @Autowired AlunoRepository alunos;
    @Autowired ProfessorRepository professores;
    @Autowired InstituicaoEnsinoRepository instituicoes;
    @Autowired EmpresaParceiraRepository empresas;
    @Autowired VantagemRepository vantagens;
    @Autowired TransacaoEnvioRepository envios;
    @Autowired TransacaoResgateRepository resgates;
    @Autowired ProfessorService professorService;
    @Autowired ResgateService resgateService;
    @Autowired AlunoService alunoService;

    @Test void resgatesSimultaneosNaoGastamMaisQueOSaldo() throws Exception {
        Aluno aluno = aluno(100);
        Vantagem vantagem = vantagem(80);
        var resultados = simultaneos(8, () -> {
            try { resgateService.resgatar(aluno.getId(), vantagem.getId()); return true; }
            catch (IllegalStateException ex) { assertEquals("Saldo insuficiente", ex.getMessage()); return false; }
        });
        assertEquals(1, resultados.stream().filter(Boolean::booleanValue).count());
        assertEquals(20, alunos.findById(aluno.getId()).orElseThrow().getSaldoMoedas());
        assertEquals(1, resgates.findByAlunoId(aluno.getId()).size());
    }

    @Test void enviosSimultaneosNaoGastamMaisQueOSaldoDoProfessor() throws Exception {
        Professor professor = professor(100);
        Aluno aluno = aluno(0);
        var resultados = simultaneos(8, () -> {
            try { professorService.enviarMoedas(professor.getId(), aluno.getId(), 80, "Mérito"); return true; }
            catch (IllegalStateException ex) { assertEquals("Saldo insuficiente", ex.getMessage()); return false; }
        });
        assertEquals(1, resultados.stream().filter(Boolean::booleanValue).count());
        assertEquals(20, professores.findById(professor.getId()).orElseThrow().getSaldoMoedas());
        assertEquals(80, alunos.findById(aluno.getId()).orElseThrow().getSaldoMoedas());
        assertEquals(1, envios.findByProfessorId(professor.getId()).size());
    }

    @Test void recebimentosSimultaneosDeProfessoresDistintosNaoSePerdem() throws Exception {
        Aluno aluno = aluno(0);
        List<Professor> remetentes = IntStream.range(0, 8).mapToObj(i -> professor(100)).toList();
        var fila = new ConcurrentLinkedQueue<>(remetentes);
        simultaneos(8, () -> professorService.enviarMoedas(fila.remove().getId(), aluno.getId(), 10, "Mérito"));
        assertEquals(80, alunos.findById(aluno.getId()).orElseThrow().getSaldoMoedas());
        assertEquals(8, envios.findByAlunoId(aluno.getId()).size());
    }

    @Test void creditoRecuperaSemestresSemAcessoUmaUnicaVezSobConcorrencia() throws Exception {
        Professor professor = professor(100);
        String atual = PeriodoLetivo.atual();
        professor.setUltimoPeriodoCreditado((Integer.parseInt(atual.substring(0, 4)) - 1) + atual.substring(4));
        professores.save(professor);
        var creditos = simultaneos(8, () -> professorService.creditarSemestreSePendente(professor.getId()));
        assertEquals(2000, creditos.stream().mapToInt(Integer::intValue).sum());
        assertEquals(2100, professores.findById(professor.getId()).orElseThrow().getSaldoMoedas());
        assertEquals(atual, professores.findById(professor.getId()).orElseThrow().getUltimoPeriodoCreditado());
    }

    @Test void primeiroCreditoIncluiSemestreDeCadastroMesmoSemLogin() {
        Professor professor = professor(0);
        int ano = Integer.parseInt(PeriodoLetivo.atual().substring(0, 4));
        int mes = PeriodoLetivo.atual().endsWith("1") ? 1 : 7;
        professor.setDataCadastro(LocalDate.of(ano - 1, mes, 1));
        professores.save(professor);
        assertEquals(3000, professorService.creditarSemestreSePendente(professor.getId()));
        assertEquals(0, professorService.creditarSemestreSePendente(professor.getId()));
    }

    @Test void rotinaCreditaSemExigirLogin() {
        Professor professor = professor(15);
        new CreditoSemestralJob(professores, professorService).creditarPendentes();
        assertEquals(1015, professores.findById(professor.getId()).orElseThrow().getSaldoMoedas());
    }

    @Test void cadastroLegadoSemDataNaoInventaSemestresAnteriores() {
        Professor professor = professor(15);
        professor.setDataCadastro(null);
        professores.save(professor);
        assertEquals(1000, professorService.creditarSemestreSePendente(professor.getId()));
    }

    @Test void editarCadastroDuranteResgatePreservaSaldo() throws Exception {
        Aluno aluno = aluno(100);
        Vantagem vantagem = vantagem(80);
        Aluno formulario = new Aluno("Nome novo", aluno.getEmail(), "", aluno.getCpf(), "RG2", "Rua B", "Curso", aluno.getInstituicao());
        formulario.setId(aluno.getId());
        formulario.setSaldoMoedas(99999);
        Queue<Callable<Object>> tarefas = new ConcurrentLinkedQueue<>(List.of(
                () -> alunoService.salvar(formulario, aluno.getInstituicao().getId()),
                () -> resgateService.resgatar(aluno.getId(), vantagem.getId())));
        simultaneos(2, () -> tarefas.remove().call());
        Aluno salvo = alunos.findById(aluno.getId()).orElseThrow();
        assertEquals(20, salvo.getSaldoMoedas());
        assertEquals("Nome novo", salvo.getNome());
    }

    private Aluno aluno(int saldo) {
        String id = UUID.randomUUID().toString();
        Aluno a = new Aluno("Aluno", id+"@test.local", "senha", id.substring(0, 14), "RG", "Rua", "Curso", instituicoes.findAll().get(0));
        a.setSaldoMoedas(saldo);
        return alunos.save(a);
    }

    private Professor professor(int saldo) {
        String id = UUID.randomUUID().toString();
        Professor p = new Professor("Professor", id+"@test.local", "senha", id.substring(0, 14), "Depto", instituicoes.findAll().get(0));
        p.setSaldoMoedas(saldo);
        return professores.save(p);
    }

    private Vantagem vantagem(int custo) {
        var e = empresas.save(new EmpresaParceira("Empresa", UUID.randomUUID()+"@test.local", "senha", null));
        var v = new Vantagem();
        v.setDescricao("Vantagem"); v.setFotoUrl("/foto.png"); v.setCustoEmMoedas(custo); v.setEmpresa(e);
        return vantagens.save(v);
    }

    private <T> List<T> simultaneos(int quantidade, Callable<T> tarefa) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(quantidade);
        CountDownLatch prontos = new CountDownLatch(quantidade);
        CountDownLatch inicio = new CountDownLatch(1);
        try {
            List<Future<T>> futuros = new ArrayList<>();
            for (int i = 0; i < quantidade; i++) futuros.add(pool.submit(() -> {
                prontos.countDown();
                if (!inicio.await(15, TimeUnit.SECONDS)) throw new IllegalStateException("Timeout aguardando início");
                return tarefa.call();
            }));
            assertTrue(prontos.await(15, TimeUnit.SECONDS));
            inicio.countDown();
            List<T> resultados = new ArrayList<>();
            for (Future<T> futuro : futuros) resultados.add(futuro.get(30, TimeUnit.SECONDS));
            return resultados;
        } finally { inicio.countDown(); pool.shutdownNow(); }
    }
}
