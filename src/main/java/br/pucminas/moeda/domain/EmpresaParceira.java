package br.pucminas.moeda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa_parceira")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class EmpresaParceira extends Usuario {

    @Column(length = 18)
    private String cnpj;

    public EmpresaParceira() {
    }

    public EmpresaParceira(String nome, String email, String senha, String cnpj) {
        super(nome, email, senha);
        this.cnpj = cnpj;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
