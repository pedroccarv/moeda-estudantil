package br.pucminas.moeda.web;

import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.service.EmailService;
import br.pucminas.moeda.service.PeriodoLetivo;
import br.pucminas.moeda.service.ProfessorService;
import br.pucminas.moeda.web.dto.Contratos;
import br.pucminas.moeda.web.sessao.SessaoGuard;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
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

    @GetMapping("/api/professor")
    public Contratos.ProfessorDetalhe painel(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.PROFESSOR);
        String aviso = creditar(sessao.getId());
        return Contratos.ProfessorDetalhe.de(professorService.buscar(sessao.getId()), aviso);
    }

    @GetMapping("/api/professor/alunos")
    public List<Contratos.AlunoOpcao> alunos(HttpSession session) {
        SessaoGuard.exigir(session, SessaoUsuario.Papel.PROFESSOR);
        return alunoService.listar().stream().map(Contratos.AlunoOpcao::de).toList();
    }

    @PostMapping("/api/professor/envios")
    public ResponseEntity<?> enviar(@RequestBody(required = false) Contratos.EnvioPedido pedido, HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.PROFESSOR);
        String aviso = creditar(sessao.getId());
        if (pedido == null || pedido.alunoId() == null || pedido.valor() == null
                || pedido.motivo() == null || pedido.motivo().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Contratos.Mensagem.de("Informe o aluno, um valor positivo e o motivo.", aviso));
        }
        professorService.enviarMoedas(sessao.getId(), pedido.alunoId(), pedido.valor(), pedido.motivo());
        String mensagem = "Moedas enviadas. Notificação registrada para envio ao aluno.";
        if (aviso != null) {
            mensagem = aviso + " " + mensagem;
        }
        Integer saldo = professorService.buscar(sessao.getId()).getSaldoMoedas();
        return ResponseEntity.ok(new Contratos.EnvioFeito(mensagem, aviso, saldo == null ? 0 : saldo));
    }

    @GetMapping("/api/professor/extrato")
    public Contratos.ExtratoProfessor extrato(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.PROFESSOR);
        String aviso = creditar(sessao.getId());
        var professor = professorService.buscar(sessao.getId());
        return new Contratos.ExtratoProfessor(
                professor.getSaldoMoedas() == null ? 0 : professor.getSaldoMoedas(),
                aviso,
                professorService.extrato(sessao.getId()).stream().map(Contratos.EnvioLinha::de).toList(),
                emailService.listarPorDestinatario(sessao.getEmail()).stream().map(Contratos.EmailItem::de).toList());
    }

    private String creditar(Long professorId) {
        int creditadas = professorService.creditarSemestreSePendente(professorId);
        if (creditadas <= 0) {
            return null;
        }
        return "Foram creditadas " + creditadas + " moedas dos períodos pendentes até " + PeriodoLetivo.rotulo()
                + ". O saldo que já existia foi mantido.";
    }
}
