package br.pucminas.moeda.web.sessao;

import jakarta.servlet.http.HttpSession;

public final class SessaoGuard {

    private SessaoGuard() {
    }

    public static SessaoUsuario atual(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object valor = session.getAttribute(SessaoUsuario.CHAVE);
        if (valor instanceof SessaoUsuario sessao) {
            return sessao;
        }
        return null;
    }

    public static SessaoUsuario exigir(HttpSession session, SessaoUsuario.Papel papel) {
        SessaoUsuario sessao = atual(session);
        if (sessao == null) {
            throw new NaoAutenticadoException();
        }
        if (sessao.getPapel() != papel) {
            throw new AcessoNegadoException();
        }
        return sessao;
    }
}
