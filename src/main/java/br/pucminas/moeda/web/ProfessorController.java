package br.pucminas.moeda.web;

import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.service.EmailService;
import br.pucminas.moeda.service.PeriodoLetivo;
import br.pucminas.moeda.service.ProfessorService;
import br.pucminas.moeda.web.sessao.ModeloGlobal;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final EmailService emailService;

    public ProfessorController(ProfessorService professorService,
                               AlunoService alunoService,
                               EmailService emailService) {
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.emailService = emailService;
    }

    @GetMapping
    public String painel(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.PROFESSOR, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        avisarCredito(sessao.getId(), model);
        model.addAttribute("professor", professorService.buscar(sessao.getId()));
        return "professor/painel";
    }

    @GetMapping("/enviar")
    public String enviarForm(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.PROFESSOR, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        avisarCredito(sessao.getId(), model);
        model.addAttribute("professor", professorService.buscar(sessao.getId()));
        model.addAttribute("alunos", alunoService.listar());
        return "professor/enviar";
    }

    @PostMapping("/enviar")
    public String enviar(@RequestParam(required = false) Long alunoId,
                         @RequestParam(required = false) Integer valor,
                         @RequestParam(required = false) String motivo,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.PROFESSOR, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        boolean creditou = professorService.creditarSemestreSePendente(sessao.getId());
        if (alunoId == null || valor == null || motivo == null || motivo.isBlank()) {
            model.addAttribute("erro", "Informe o aluno, um valor positivo e o motivo.");
            if (creditou) {
                model.addAttribute("sucesso",
                        "Foram creditadas 1.000 moedas do semestre " + PeriodoLetivo.rotulo()
                                + ". O saldo que já existia foi mantido.");
            }
            model.addAttribute("professor", professorService.buscar(sessao.getId()));
            model.addAttribute("alunos", alunoService.listar());
            model.addAttribute("alunoId", alunoId);
            model.addAttribute("valor", valor);
            model.addAttribute("motivo", motivo);
            return "professor/enviar";
        }
        try {
            professorService.enviarMoedas(sessao.getId(), alunoId, valor, motivo);
            String mensagem = "Moedas enviadas. O aluno foi notificado por e-mail.";
            if (creditou) {
                mensagem = "Foram creditadas 1.000 moedas do semestre " + PeriodoLetivo.rotulo() + ". " + mensagem;
            }
            redirect.addFlashAttribute("sucesso", mensagem);
            return "redirect:/professor/extrato";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("professor", professorService.buscar(sessao.getId()));
            model.addAttribute("alunos", alunoService.listar());
            model.addAttribute("alunoId", alunoId);
            model.addAttribute("valor", valor);
            model.addAttribute("motivo", motivo);
            return "professor/enviar";
        }
    }

    @GetMapping("/extrato")
    public String extrato(HttpSession session, Model model, RedirectAttributes redirect) {
        String bloqueio = ModeloGlobal.exigir(session, SessaoUsuario.Papel.PROFESSOR, redirect);
        if (bloqueio != null) {
            return bloqueio;
        }
        SessaoUsuario sessao = ModeloGlobal.atual(session);
        avisarCredito(sessao.getId(), model);
        model.addAttribute("professor", professorService.buscar(sessao.getId()));
        model.addAttribute("envios", professorService.extrato(sessao.getId()));
        model.addAttribute("emails", emailService.listarPorDestinatario(sessao.getEmail()));
        return "professor/extrato";
    }

    private void avisarCredito(Long professorId, Model model) {
        if (professorService.creditarSemestreSePendente(professorId)) {
            model.addAttribute("sucesso",
                    "Foram creditadas 1.000 moedas do semestre " + PeriodoLetivo.rotulo()
                            + ". O saldo que já existia foi mantido.");
        }
    }
}
