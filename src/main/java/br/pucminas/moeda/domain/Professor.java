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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;

    public Professor() {
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

    public InstituicaoEnsino getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(InstituicaoEnsino instituicao) {
        this.instituicao = instituicao;
    }
}
