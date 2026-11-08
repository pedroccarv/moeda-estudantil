package br.pucminas.moeda.web;

import br.pucminas.moeda.service.EmailService;
import br.pucminas.moeda.service.EmpresaParceiraService;
import br.pucminas.moeda.service.ResgateService;
import br.pucminas.moeda.service.VantagemService;
import br.pucminas.moeda.web.sessao.ModeloGlobal;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empresa")
public class EmpresaPainelController {

    private final EmpresaParceiraService empresaService;
    private final VantagemService vantagemService;
    private final ResgateService resgateService;
    private final EmailService emailService;

    public EmpresaPainelController(EmpresaParceiraService empresaService,
                                   VantagemService vantagemService,
                                   ResgateService resgateService,
                                   EmailService emailService) {
        this.empresaService = empresaService;
        this.vantagemService = vantagemService;
        this.resgateService = resgateService;
        this.emailService = emailService;
    }

    @GetMapping
    public String painel(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.EMPRESA, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        model.addAttribute("empresa", empresaService.buscarPorId(sessao.getId()));
        model.addAttribute("vantagens", vantagemService.listarDaEmpresa(sessao.getId()));
        model.addAttribute("resgates", resgateService.resgatesDaEmpresa(sessao.getId()));
        model.addAttribute("emails", emailService.listarPorDestinatario(sessao.getEmail()));
        return "empresa/painel";
    }

    @GetMapping("/vantagens/nova")
    public String nova(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.EMPRESA, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        return "empresa/vantagem-form";
    }

    @GetMapping("/vantagens/{id}/editar")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.EMPRESA, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        try {
            model.addAttribute("vantagem", vantagemService.buscarDaEmpresa(ModeloGlobal.atual(session).getId(), id));
            return "empresa/vantagem-form";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/empresa";
        }
    }

    @PostMapping("/vantagens/salvar")
    public String salvar(@RequestParam(required = false) Long id,
                         @RequestParam String descricao,
                         @RequestParam Integer custoEmMoedas,
                         @RequestParam(required = false) MultipartFile foto,
                         HttpSession session,
                         RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.EMPRESA, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        try {
            vantagemService.salvar(ModeloGlobal.atual(session).getId(), id, descricao, custoEmMoedas, foto);
            redirect.addFlashAttribute("sucesso", id == null ? "Vantagem cadastrada." : "Vantagem atualizada.");
            return "redirect:/empresa";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            if (id == null) {
                return "redirect:/empresa/vantagens/nova";
            }
            return "redirect:/empresa/vantagens/" + id + "/editar";
        }
    }

    @PostMapping("/vantagens/{id}/excluir")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.EMPRESA, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        try {
            vantagemService.excluir(ModeloGlobal.atual(session).getId(), id);
            redirect.addFlashAttribute("sucesso", "Vantagem excluída.");
        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: alunos já resgataram esta vantagem.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/empresa";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String arquivoGrande(RedirectAttributes redirect) {
        redirect.addFlashAttribute("erro", "A foto deve ter no máximo 5 MB.");
        return "redirect:/empresa/vantagens/nova";
    }
}
