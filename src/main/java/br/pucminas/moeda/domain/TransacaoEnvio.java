package br.pucminas.moeda.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "transacao_envio")
@PrimaryKeyJoinColumn(name = "transacao_id")
public class TransacaoEnvio extends Transacao {

    @Column(nullable = false, length = 500)
    private String motivo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    public TransacaoEnvio() {
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}
