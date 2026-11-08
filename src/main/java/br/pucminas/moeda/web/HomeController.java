package br.pucminas.moeda.web;

import br.pucminas.moeda.web.sessao.ModeloGlobal;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        if (sessao != null) {
            return "redirect:" + sessao.getDestino();
        }
        return "index";
    }
}
