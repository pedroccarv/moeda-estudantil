package br.pucminas.moeda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "professor")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Professor extends Usuario {

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String departamento;

    @Column(nullable = false)
    private Integer saldoMoedas = 0;

    @Column(length = 10)
    private String ultimoPeriodoCreditado;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;

    public Professor() {
    }

    public Professor(String nome, String email, String senha, String cpf,
                     String departamento, InstituicaoEnsino instituicao) {
        super(nome, email, senha);
        this.cpf = cpf;
        this.departamento = departamento;
        this.instituicao = instituicao;
        this.saldoMoedas = 0;
    }

    public void creditarSemestre(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do semestre deve ser positiva");
        }
        if (this.saldoMoedas == null) {
            this.saldoMoedas = 0;
        }
        this.saldoMoedas += quantidade;
    }

    public TransacaoEnvio enviarMoedas(Aluno aluno, Integer valor, String motivo) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno é obrigatório");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório");
        }
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        if (this.saldoMoedas == null) {
            this.saldoMoedas = 0;
        }
        if (this.saldoMoedas < valor) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        this.saldoMoedas -= valor;
        aluno.creditar(valor);
        return new TransacaoEnvio(valor, motivo.trim(), this, aluno);
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Integer getSaldoMoedas() {
        return saldoMoedas;
    }

    public void setSaldoMoedas(Integer saldoMoedas) {
        this.saldoMoedas = saldoMoedas;
    }

    public String getUltimoPeriodoCreditado() {
        return ultimoPeriodoCreditado;
    }

    public void setUltimoPeriodoCreditado(String ultimoPeriodoCreditado) {
        this.ultimoPeriodoCreditado = ultimoPeriodoCreditado;
    }

    public InstituicaoEnsino getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(InstituicaoEnsino instituicao) {
        this.instituicao = instituicao;
    }
}
