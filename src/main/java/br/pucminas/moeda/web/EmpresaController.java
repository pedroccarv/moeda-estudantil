package br.pucminas.moeda.web;

import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.service.EmailService;
import br.pucminas.moeda.service.EmpresaParceiraService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EmpresaController {

    private final EmpresaParceiraService empresaService;
    private final VantagemService vantagemService;
    private final ResgateService resgateService;
    private final EmailService emailService;

    public EmpresaController(EmpresaParceiraService empresaService,
                             VantagemService vantagemService,
                             ResgateService resgateService,
                             EmailService emailService) {
        this.empresaService = empresaService;
        this.vantagemService = vantagemService;
        this.resgateService = resgateService;
        this.emailService = emailService;
    }

    @PostMapping("/api/empresas")
    public ResponseEntity<Contratos.Mensagem> cadastrar(@RequestBody(required = false) Contratos.EmpresaPedido pedido) {
        empresaService.salvar(entidade(pedido, null));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Contratos.Mensagem.de("Cadastro realizado. Entre para cadastrar as vantagens."));
    }

    @GetMapping("/api/empresa")
    public Contratos.PainelEmpresa painel(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        return new Contratos.PainelEmpresa(
                Contratos.EmpresaDetalhe.de(empresaService.buscarPorId(sessao.getId())),
                vantagemService.listarDaEmpresa(sessao.getId()).stream().map(Contratos.VantagemItem::de).toList(),
                resgateService.resgatesDaEmpresa(sessao.getId()).stream().map(Contratos.ResgateLinha::de).toList(),
                emailService.listarPorDestinatario(sessao.getEmail()).stream().map(Contratos.EmailItem::de).toList());
    }

    @PutMapping("/api/empresa")
    public Contratos.Mensagem atualizar(@RequestBody(required = false) Contratos.EmpresaPedido pedido, HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        EmpresaParceira salva = empresaService.salvar(entidade(pedido, sessao.getId()));
        session.setAttribute(SessaoUsuario.CHAVE,
                new SessaoUsuario(salva.getId(), salva.getNome(), salva.getEmail(), SessaoUsuario.Papel.EMPRESA));
        return Contratos.Mensagem.de("Dados atualizados.");
    }

    @DeleteMapping("/api/empresa")
    public ResponseEntity<Contratos.Mensagem> excluir(HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        try {
            empresaService.excluir(sessao.getId());
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Contratos.Mensagem.de("Não é possível excluir: existem vantagens ou resgates vinculados."));
        }
        session.invalidate();
        return ResponseEntity.ok(Contratos.Mensagem.de("Cadastro excluído."));
    }

    @GetMapping("/api/empresa/vantagens/{id}")
    public Contratos.VantagemItem vantagem(@PathVariable Long id, HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        return Contratos.VantagemItem.de(vantagemService.buscarDaEmpresa(sessao.getId(), id));
    }

    @PostMapping("/api/empresa/vantagens")
    public ResponseEntity<Contratos.Mensagem> criar(@RequestParam String descricao,
                                                    @RequestParam Integer custoEmMoedas,
                                                    @RequestParam(required = false) MultipartFile foto,
                                                    HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        vantagemService.salvar(sessao.getId(), null, descricao, custoEmMoedas, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Contratos.Mensagem.de("Vantagem cadastrada."));
    }

    @PostMapping("/api/empresa/vantagens/{id}")
    public Contratos.Mensagem atualizarVantagem(@PathVariable Long id,
                                                @RequestParam String descricao,
                                                @RequestParam Integer custoEmMoedas,
                                                @RequestParam(required = false) MultipartFile foto,
                                                HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        vantagemService.salvar(sessao.getId(), id, descricao, custoEmMoedas, foto);
        return Contratos.Mensagem.de("Vantagem atualizada.");
    }

    @DeleteMapping("/api/empresa/vantagens/{id}")
    public ResponseEntity<Contratos.Mensagem> excluirVantagem(@PathVariable Long id, HttpSession session) {
        SessaoUsuario sessao = SessaoGuard.exigir(session, SessaoUsuario.Papel.EMPRESA);
        try {
            vantagemService.excluir(sessao.getId(), id);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Contratos.Mensagem.de("Não é possível excluir: alunos já resgataram esta vantagem."));
        }
        return ResponseEntity.ok(Contratos.Mensagem.de("Vantagem excluída."));
    }

    private EmpresaParceira entidade(Contratos.EmpresaPedido pedido, Long id) {
        if (pedido == null) {
            throw new IllegalArgumentException("Preencha os dados da empresa");
        }
        EmpresaParceira empresa = new EmpresaParceira();
        empresa.setId(id);
        empresa.setNome(pedido.nome());
        empresa.setEmail(pedido.email());
        empresa.setSenha(pedido.senha());
        empresa.setCnpj(pedido.cnpj());
        return empresa;
    }
}
