package br.pucminas.moeda.web;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import br.pucminas.moeda.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final InstituicaoEnsinoRepository instituicaoRepository;

    public AlunoController(AlunoService alunoService,
                           InstituicaoEnsinoRepository instituicaoRepository) {
        this.alunoService = alunoService;
        this.instituicaoRepository = instituicaoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.listar());
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "alunos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("aluno", alunoService.buscarPorId(id));
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "alunos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("aluno") Aluno aluno,
                         BindingResult bindingResult,
                         @RequestParam Long instituicaoId,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "alunos/form";
        }
        try {
            alunoService.salvar(aluno, instituicaoId);
            redirectAttributes.addFlashAttribute("sucesso", "Aluno salvo com sucesso.");
            return "redirect:/alunos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "alunos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Aluno excluído.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/alunos";
    }
}
