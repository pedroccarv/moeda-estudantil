package br.pucminas.moeda.web;

import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.service.EmpresaParceiraService;
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
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaParceiraService empresaService;

    public EmpresaController(EmpresaParceiraService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public String listar(HttpSession session) {
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        if (sessao == null) {
            return "redirect:/login";
        }
        return "redirect:" + sessao.getDestino();
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        if (!model.containsAttribute("empresa")) {
            model.addAttribute("empresa", new EmpresaParceira());
        }
        return "empresas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = dono(session, id, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        model.addAttribute("empresa", empresaService.buscarPorId(id));
        return "empresas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("empresa") EmpresaParceira empresa,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirect) {
        if (empresa.getId() != null) {
            String bloqueio = dono(session, empresa.getId(), redirect);
            if (bloqueio != null) {
                return bloqueio;
            }
        }
        if (bindingResult.hasErrors()) {
            return "empresas/form";
        }
        try {
            boolean nova = empresa.getId() == null;
            empresaService.salvar(empresa);
            if (nova) {
                redirect.addFlashAttribute("sucesso",
                        "Cadastro realizado. Entre para cadastrar as vantagens.");
                return "redirect:/login";
            }
            redirect.addFlashAttribute("sucesso", "Dados atualizados.");
            return "redirect:/empresa";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "empresas/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        String bloqueio = dono(session, id, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        try {
            empresaService.excluir(id);
            session.invalidate();
            redirect.addFlashAttribute("sucesso", "Cadastro excluído.");
            return "redirect:/login";
        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("erro",
                    "Não é possível excluir: existem vantagens ou resgates vinculados.");
            return "redirect:/empresa";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/empresa";
        }
    }

    private String dono(HttpSession session, Long id, RedirectAttributes redirect) {
        String papel = ModeloGlobal.exigir(session, SessaoUsuario.Papel.EMPRESA, redirect);
        if (papel != null) {
            return papel;
        }
        if (!ModeloGlobal.atual(session).getId().equals(id)) {
            redirect.addFlashAttribute("erro", "Você só pode alterar o próprio cadastro.");
            return "redirect:/empresa";
        }
        return null;
    }
}
