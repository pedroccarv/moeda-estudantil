package br.pucminas.moeda.web;

import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.service.EmailService;
import br.pucminas.moeda.service.ResgateService;
import br.pucminas.moeda.service.VantagemService;
import br.pucminas.moeda.web.sessao.ModeloGlobal;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aluno")
public class AlunoPainelController {

    private final AlunoService alunoService;
    private final VantagemService vantagemService;
    private final ResgateService resgateService;
    private final EmailService emailService;

    public AlunoPainelController(AlunoService alunoService,
                                 VantagemService vantagemService,
                                 ResgateService resgateService,
                                 EmailService emailService) {
        this.alunoService = alunoService;
        this.vantagemService = vantagemService;
        this.resgateService = resgateService;
        this.emailService = emailService;
    }

    @GetMapping
    public String painel(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.ALUNO, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        model.addAttribute("aluno", alunoService.buscarPorId(sessao.getId()));
        return "aluno/painel";
    }

    @GetMapping("/extrato")
    public String extrato(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.ALUNO, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        model.addAttribute("aluno", alunoService.buscarPorId(sessao.getId()));
        model.addAttribute("recebimentos", alunoService.recebimentos(sessao.getId()));
        model.addAttribute("resgates", resgateService.extratoDoAluno(sessao.getId()));
        model.addAttribute("emails", emailService.listarPorDestinatario(sessao.getEmail()));
        return "aluno/extrato";
    }

    @GetMapping("/vantagens")
    public String vantagens(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.ALUNO, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        model.addAttribute("aluno", alunoService.buscarPorId(sessao.getId()));
        model.addAttribute("vantagens", vantagemService.listarTodas());
        return "aluno/vantagens";
    }

    @PostMapping("/vantagens/{id}/resgatar")
    public String resgatar(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.ALUNO, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        try {
            var resgate = resgateService.resgatar(sessao.getId(), id);
            redirect.addFlashAttribute("sucesso",
                    "Resgate concluído. Código do cupom: " + resgate.getCodigoCupom()
                            + ". As notificações para você e para a empresa foram registradas para envio.");
            return "redirect:/aluno/extrato";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/aluno/vantagens";
        }
    }
}
