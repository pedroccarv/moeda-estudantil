package br.pucminas.moeda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "aluno")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Aluno extends Usuario {

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 20)
    private String rg;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false, length = 100)
    private String curso;

    @Column(nullable = false)
    private Integer saldoMoedas = 0;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;

    public Aluno() {
    }

    public Aluno(String nome, String email, String senha, String cpf, String rg,
                 String endereco, String curso, InstituicaoEnsino instituicao) {
        super(nome, email, senha);
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
        this.curso = curso;
        this.instituicao = instituicao;
        this.saldoMoedas = 0;
    }

    public void creditar(Integer valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("Valor de crédito deve ser positivo");
        }
        this.saldoMoedas += valor;
    }

    public void debitar(Integer valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("Valor de débito deve ser positivo");
        }
        if (this.saldoMoedas < valor) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        this.saldoMoedas -= valor;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getSaldoMoedas() {
        return saldoMoedas;
    }

    public void setSaldoMoedas(Integer saldoMoedas) {
        this.saldoMoedas = saldoMoedas;
    }

    public InstituicaoEnsino getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(InstituicaoEnsino instituicao) {
        this.instituicao = instituicao;
    }
}
