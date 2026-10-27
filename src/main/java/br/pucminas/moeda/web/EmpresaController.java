package br.pucminas.moeda.web;

import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.service.EmpresaParceiraService;
import jakarta.validation.Valid;
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
    public String listar(Model model) {
        model.addAttribute("empresas", empresaService.listar());
        return "empresas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("empresa", new EmpresaParceira());
        return "empresas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("empresa", empresaService.buscarPorId(id));
        return "empresas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("empresa") EmpresaParceira empresa,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "empresas/form";
        }
        try {
            empresaService.salvar(empresa);
            redirectAttributes.addFlashAttribute("sucesso", "Empresa salva com sucesso.");
            return "redirect:/empresas";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "empresas/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empresaService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Empresa excluída.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/empresas";
    }
}
