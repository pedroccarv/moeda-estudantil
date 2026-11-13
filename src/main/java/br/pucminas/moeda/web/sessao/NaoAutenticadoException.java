package br.pucminas.moeda.web.sessao;

public class NaoAutenticadoException extends RuntimeException {

    public NaoAutenticadoException() {
        super("Entre para continuar.");
    }
}
