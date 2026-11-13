package br.pucminas.moeda.web;

import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import br.pucminas.moeda.web.dto.Contratos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InstituicaoController {

    private final InstituicaoEnsinoRepository instituicoes;

    public InstituicaoController(InstituicaoEnsinoRepository instituicoes) {
        this.instituicoes = instituicoes;
    }

    @GetMapping("/api/instituicoes")
    public List<Contratos.Instituicao> listar() {
        return instituicoes.findAll().stream().map(Contratos.Instituicao::de).toList();
    }
}
