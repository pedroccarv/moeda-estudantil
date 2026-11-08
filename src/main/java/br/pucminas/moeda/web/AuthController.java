package br.pucminas.moeda.web;

import br.pucminas.moeda.service.AuthService;
import br.pucminas.moeda.web.sessao.ModeloGlobal;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String formulario(HttpSession session) {
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        if (sessao != null) {
            return "redirect:" + sessao.getDestino();
        }
        return "login";
    }

    @PostMapping("/login")
    public String entrar(@RequestParam String email,
                         @RequestParam String senha,
                         HttpServletRequest request,
                         Model model) {
        try {
            SessaoUsuario sessao = authService.autenticar(email, senha);
            HttpSession antiga = request.getSession(false);
            if (antiga != null) {
                antiga.invalidate();
            }
            request.getSession(true).setAttribute(SessaoUsuario.CHAVE, sessao);
            return "redirect:" + sessao.getDestino();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @PostMapping("/logout")
    public String sair(HttpServletRequest request, RedirectAttributes redirect) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect.addFlashAttribute("sucesso", "Sessão encerrada.");
        return "redirect:/login";
    }
}
