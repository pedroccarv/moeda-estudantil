package br.pucminas.moeda.web;

import br.pucminas.moeda.service.AuthService;
import br.pucminas.moeda.web.dto.Contratos;
import br.pucminas.moeda.web.sessao.NaoAutenticadoException;
import br.pucminas.moeda.web.sessao.SessaoGuard;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessaoController {

    private final AuthService authService;

    public SessaoController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/sessao")
    public Contratos.Sessao atual(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.atual(session);
        if (sessao == null) {
            throw new NaoAutenticadoException();
        }
        return Contratos.Sessao.de(sessao);
    }

    @PostMapping("/api/sessao")
    public Contratos.Sessao entrar(@RequestBody(required = false) Contratos.Login pedido, HttpServletRequest request) {
        if (pedido == null) {
            throw new IllegalArgumentException("Informe e-mail e senha");
        }
        SessaoUsuario sessao = authService.autenticar(pedido.email(), pedido.senha());
        HttpSession antiga = request.getSession(false);
        if (antiga != null) {
            antiga.invalidate();
        }
        request.getSession(true).setAttribute(SessaoUsuario.CHAVE, sessao);
        return Contratos.Sessao.de(sessao);
    }

    @DeleteMapping("/api/sessao")
    public Contratos.Mensagem sair(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Contratos.Mensagem.de("Sessão encerrada.");
    }
}
