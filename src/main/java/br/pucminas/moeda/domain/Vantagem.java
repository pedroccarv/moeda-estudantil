package br.pucminas.moeda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "vantagem")
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, length = 500)
    private String fotoUrl;

    @Column(nullable = false)
    private Integer custoEmMoedas;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaParceira empresa;

    public Vantagem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Integer getCustoEmMoedas() {
        return custoEmMoedas;
    }

    public void setCustoEmMoedas(Integer custoEmMoedas) {
        this.custoEmMoedas = custoEmMoedas;
    }

    public EmpresaParceira getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaParceira empresa) {
        this.empresa = empresa;
    }
}
