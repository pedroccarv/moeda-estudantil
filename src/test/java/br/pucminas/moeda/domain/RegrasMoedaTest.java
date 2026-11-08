package br.pucminas.moeda.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegrasMoedaTest {

    @Test
    void enviarMoedasDebitaProfessorECreditaAluno() {
        InstituicaoEnsino inst = new InstituicaoEnsino("PUC Minas");
        Professor professor = professor(inst, 1000);
        Aluno aluno = aluno(inst);

        TransacaoEnvio envio = professor.enviarMoedas(aluno, 100, "Participação em aula");

        assertEquals(900, professor.getSaldoMoedas());
        assertEquals(100, aluno.getSaldoMoedas());
        assertEquals("Participação em aula", envio.getMotivo());
        assertEquals(100, envio.getValor());
    }

    @Test
    void enviarRecusaSaldoInsuficienteEMotivoVazio() {
        InstituicaoEnsino inst = new InstituicaoEnsino("PUC Minas");
        Professor professor = professor(inst, 50);
        Aluno aluno = aluno(inst);

        assertThrows(IllegalStateException.class, () -> professor.enviarMoedas(aluno, 51, "Mérito"));
        assertThrows(IllegalArgumentException.class, () -> professor.enviarMoedas(aluno, 10, "  "));
        assertEquals(50, professor.getSaldoMoedas());
        assertEquals(0, aluno.getSaldoMoedas());
    }

    @Test
    void creditoSemestralSomaAoSaldoExistente() {
        Professor professor = professor(new InstituicaoEnsino("PUC Minas"), 200);
        professor.creditarSemestre(1000);
        assertEquals(1200, professor.getSaldoMoedas());
    }

    @Test
    void alunoNaoDebitaAlemDoSaldo() {
        Aluno aluno = aluno(new InstituicaoEnsino("PUC Minas"));
        aluno.creditar(30);
        assertThrows(IllegalStateException.class, () -> aluno.debitar(31));
        assertEquals(30, aluno.getSaldoMoedas());
    }

    @Test
    void vantagemExigeDescricaoFotoECusto() {
        EmpresaParceira empresa = new EmpresaParceira("Cantina", "c@x.com", "123", null);
        Vantagem vantagem = new Vantagem();
        vantagem.setDescricao("Desconto");
        vantagem.setCustoEmMoedas(10);
        assertThrows(IllegalArgumentException.class, () -> empresa.cadastrarVantagem(vantagem));

        vantagem.setFotoUrl("/uploads/foto.png");
        empresa.cadastrarVantagem(vantagem);
        assertEquals(empresa, vantagem.getEmpresa());
    }

    private Professor professor(InstituicaoEnsino inst, int saldo) {
        Professor professor = new Professor("Ana", "ana@x.com", "123", "1", "Computação", inst);
        professor.setSaldoMoedas(saldo);
        return professor;
    }

    private Aluno aluno(InstituicaoEnsino inst) {
        return new Aluno("Maria", "maria@x.com", "123", "2", "MG1", "Rua A", "Engenharia", inst);
    }
}
