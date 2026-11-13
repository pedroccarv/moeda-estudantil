package br.pucminas.moeda.web;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.service.AlunoService;
import br.pucminas.moeda.service.EmailService;
import br.pucminas.moeda.service.ResgateService;
import br.pucminas.moeda.service.VantagemService;
import br.pucminas.moeda.web.dto.Contratos;
import br.pucminas.moeda.web.sessao.SessaoGuard;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlunoController {

    private final AlunoService alunoService;
    private final VantagemService vantagemService;
    private final ResgateService resgateService;
    private final EmailService emailService;

    public AlunoController(AlunoService alunoService,
                           VantagemService vantagemService,
                           ResgateService resgateService,
                           EmailService emailService) {
        this.alunoService = alunoService;
        this.vantagemService = vantagemService;
        this.resgateService = resgateService;
        this.emailService = emailService;
    }

    @PostMapping("/api/alunos")
    public ResponseEntity<Contratos.Mensagem> cadastrar(@RequestBody(required = false) Contratos.AlunoPedido pedido) {
        alunoService.salvar(entidade(pedido, null), instituicaoDe(pedido));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Contratos.Mensagem.de("Cadastro realizado. Entre com seu e-mail e senha."));
    }

    @GetMapping("/api/aluno")
    public Contratos.AlunoDetalhe painel(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.ALUNO);
        return Contratos.AlunoDetalhe.de(alunoService.buscarPorId(sessao.getId()));
    }

    @PutMapping("/api/aluno")
    public Contratos.Mensagem atualizar(@RequestBody(required = false) Contratos.AlunoPedido pedido, HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.ALUNO);
        Aluno salvo = alunoService.salvar(entidade(pedido, sessao.getId()), instituicaoDe(pedido));
        session.setAttribute(SessaoUsuario.CHAVE,
                new SessaoUsuario(salvo.getId(), salvo.getNome(), salvo.getEmail(), SessaoUsuario.Papel.ALUNO));
        return Contratos.Mensagem.de("Dados atualizados.");
    }

    @DeleteMapping("/api/aluno")
    public ResponseEntity<Contratos.Mensagem> excluir(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.ALUNO);
        try {
            alunoService.excluir(sessao.getId());
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Contratos.Mensagem.de("Não é possível excluir: existem moedas ou resgates vinculados a este aluno."));
        }
        session.invalidate();
        return ResponseEntity.ok(Contratos.Mensagem.de("Cadastro excluído."));
    }

    @GetMapping("/api/aluno/extrato")
    public Contratos.ExtratoAluno extrato(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.ALUNO);
        Aluno aluno = alunoService.buscarPorId(sessao.getId());
        return new Contratos.ExtratoAluno(
                aluno.getSaldoMoedas() == null ? 0 : aluno.getSaldoMoedas(),
                alunoService.recebimentos(sessao.getId()).stream().map(Contratos.Recebimento::de).toList(),
                resgateService.extratoDoAluno(sessao.getId()).stream().map(Contratos.ResgateLinha::de).toList(),
                emailService.listarPorDestinatario(sessao.getEmail()).stream().map(Contratos.EmailItem::de).toList());
    }

    @GetMapping("/api/aluno/vantagens")
    public Contratos.VantagensAluno vantagens(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.ALUNO);
        Aluno aluno = alunoService.buscarPorId(sessao.getId());
        return new Contratos.VantagensAluno(
                aluno.getSaldoMoedas() == null ? 0 : aluno.getSaldoMoedas(),
                vantagemService.listarTodas().stream().map(Contratos.VantagemItem::de).toList());
    }

    @PostMapping("/api/aluno/vantagens/{id}/resgatar")
    public Contratos.ResgateFeito resgatar(@PathVariable Long id, HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.ALUNO);
        var resgate = resgateService.resgatar(sessao.getId(), id);
        return new Contratos.ResgateFeito(
                "Resgate concluído. Código do cupom: " + resgate.getCodigoCupom()
                        + ". As notificações para você e para a empresa foram registradas para envio.",
                resgate.getCodigoCupom());
    }

    private Aluno entidade(Contratos.AlunoPedido pedido, Long id) {
        if (pedido == null) {
            throw new IllegalArgumentException("Preencha os dados do aluno");
        }
        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setNome(pedido.nome());
        aluno.setEmail(pedido.email());
        aluno.setSenha(pedido.senha());
        aluno.setCpf(pedido.cpf());
        aluno.setRg(pedido.rg());
        aluno.setEndereco(pedido.endereco());
        aluno.setCurso(pedido.curso());
        return aluno;
    }

    private Long instituicaoDe(Contratos.AlunoPedido pedido) {
        if (pedido == null || pedido.instituicaoId() == null) {
            throw new IllegalArgumentException("Selecione a instituição");
        }
        return pedido.instituicaoId();
    }
}
