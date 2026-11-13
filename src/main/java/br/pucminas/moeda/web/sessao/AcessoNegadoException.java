package br.pucminas.moeda.web.sessao;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException() {
        super("Essa área é de outro perfil.");
    }
}
