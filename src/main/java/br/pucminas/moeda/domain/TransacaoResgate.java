package br.pucminas.moeda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "transacao_resgate")
@PrimaryKeyJoinColumn(name = "transacao_id")
public class TransacaoResgate extends Transacao {

    @Column(nullable = false, unique = true, length = 40)
    private String codigoCupom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vantagem_id", nullable = false)
    private Vantagem vantagem;

    public TransacaoResgate() {
    }

    public String getCodigoCupom() {
        return codigoCupom;
    }

    public void setCodigoCupom(String codigoCupom) {
        this.codigoCupom = codigoCupom;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Vantagem getVantagem() {
        return vantagem;
    }

    public void setVantagem(Vantagem vantagem) {
        this.vantagem = vantagem;
    }
}
