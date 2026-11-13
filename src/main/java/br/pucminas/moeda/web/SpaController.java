package br.pucminas.moeda.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
            "/",
            "/entrar",
            "/cadastro/aluno",
            "/cadastro/empresa",
            "/aluno",
            "/aluno/extrato",
            "/aluno/vantagens",
            "/aluno/editar",
            "/professor",
            "/professor/enviar",
            "/professor/extrato",
            "/empresa",
            "/empresa/editar",
            "/empresa/vantagens/nova",
            "/empresa/vantagens/{id:\\d+}"
    })
    public String pagina() {
        return "forward:/index.html";
    }
}
