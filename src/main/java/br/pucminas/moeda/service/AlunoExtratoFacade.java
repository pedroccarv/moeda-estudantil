package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.web.dto.Contratos;
import org.springframework.stereotype.Service;

@Service
public class AlunoExtratoFacade {
    private final AlunoService alunoService;
    private final ResgateService resgateService;
    private final EmailService emailService;

    public AlunoExtratoFacade(AlunoService alunoService,
                              ResgateService resgateService,
                              EmailService emailService) {
        this.alunoService = alunoService;
        this.resgateService = resgateService;
        this.emailService = emailService;
    }

    public Contratos.ExtratoAluno carregar(Long alunoId, String email) {
        Aluno aluno = alunoService.buscarPorId(alunoId);
        return new Contratos.ExtratoAluno(
                aluno.getSaldoMoedas() == null ? 0 : aluno.getSaldoMoedas(),
                alunoService.recebimentos(alunoId).stream()
                        .map(Contratos.Recebimento::de).toList(),
                resgateService.extratoDoAluno(alunoId).stream()
                        .map(Contratos.ResgateLinha::de).toList(),
                emailService.listarPorDestinatario(email).stream()
                        .map(Contratos.EmailItem::de).toList());
    }
}
