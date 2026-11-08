package br.pucminas.moeda.web.sessao;

import java.io.Serializable;

public class SessaoUsuario implements Serializable {

    public static final String CHAVE = "usuarioLogado";

    public enum Papel {
        ALUNO, PROFESSOR, EMPRESA
    }

    private final Long id;
    private final String nome;
    private final String email;
    private final Papel papel;

    public SessaoUsuario(Long id, String nome, String email, Papel papel) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.papel = papel;
    }

    public String getDestino() {
        return switch (papel) {
            case ALUNO -> "/aluno";
            case PROFESSOR -> "/professor";
            case EMPRESA -> "/empresa";
        };
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Papel getPapel() {
        return papel;
    }
}
