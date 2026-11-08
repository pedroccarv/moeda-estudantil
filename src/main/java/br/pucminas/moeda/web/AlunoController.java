package br.pucminas.moeda.web;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.web.sessao.ModeloGlobal;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
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
    public String listar(HttpSession session) {
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        if (sessao == null) {
            return "redirect:/login";
        }
        return "redirect:" + sessao.getDestino();
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("aluno")) {
            model.addAttribute("aluno", new Aluno());
        }
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "alunos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = dono(session, id, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        model.addAttribute("aluno", alunoService.buscarPorId(id));
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "alunos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("aluno") Aluno aluno,
                         BindingResult bindingResult,
                         @RequestParam Long instituicaoId,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirect) {
        if (aluno.getId() != null) {
            String bloqueio = dono(session, aluno.getId(), redirect);
            if (bloqueio != null) {
                return bloqueio;
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "alunos/form";
        }
        try {
            boolean novo = aluno.getId() == null;
            alunoService.salvar(aluno, instituicaoId);
            if (novo) {
                redirect.addFlashAttribute("sucesso",
                        "Cadastro realizado. Entre com seu e-mail e senha.");
                return "redirect:/login";
            }
            redirect.addFlashAttribute("sucesso", "Dados atualizados.");
            return "redirect:/aluno";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "alunos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        String bloqueio = dono(session, id, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        try {
            alunoService.excluir(id);
            session.invalidate();
            redirect.addFlashAttribute("sucesso", "Cadastro excluído.");
            return "redirect:/login";
        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("erro",
                    "Não é possível excluir: existem moedas ou resgates vinculados a este aluno.");
            return "redirect:/aluno";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/aluno";
        }
    }

    private String dono(HttpSession session, Long id, RedirectAttributes redirect) {
        String papel = ModeloGlobal.exigir(session, SessaoUsuario.Papel.ALUNO, redirect);
        if (papel != null) {
            return papel;
        }
        if (!ModeloGlobal.atual(session).getId().equals(id)) {
            redirect.addFlashAttribute("erro", "Você só pode alterar o próprio cadastro.");
            return "redirect:/aluno";
        }
        return null;
    }
}
