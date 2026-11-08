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

    public Vantagem cadastrarVantagem(Vantagem vantagem) {
        if (vantagem == null) {
            throw new IllegalArgumentException("Vantagem é obrigatória");
        }
        if (vantagem.getDescricao() == null || vantagem.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (vantagem.getFotoUrl() == null || vantagem.getFotoUrl().isBlank()) {
            throw new IllegalArgumentException("Foto do produto é obrigatória");
        }
        if (vantagem.getCustoEmMoedas() == null || vantagem.getCustoEmMoedas() <= 0) {
            throw new IllegalArgumentException("Custo em moedas deve ser positivo");
        }
        vantagem.setDescricao(vantagem.getDescricao().trim());
        vantagem.setEmpresa(this);
        return vantagem;
    }
}
