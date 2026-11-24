package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.TransacaoResgate;
import br.pucminas.moeda.domain.Vantagem;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.TransacaoResgateRepository;
import br.pucminas.moeda.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResgateService {

    private final AlunoRepository alunoRepository;
    private final VantagemRepository vantagemRepository;
    private final TransacaoResgateRepository resgateRepository;
    private final EmailService emailService;
    private final CupomService cupomService;
    private final EmailTemplateService emailTemplateService;

    public ResgateService(AlunoRepository alunoRepository,
                          VantagemRepository vantagemRepository,
                          TransacaoResgateRepository resgateRepository,
                          EmailService emailService,
                          CupomService cupomService,
                          EmailTemplateService emailTemplateService) {
        this.alunoRepository = alunoRepository;
        this.vantagemRepository = vantagemRepository;
        this.resgateRepository = resgateRepository;
        this.emailService = emailService;
        this.cupomService = cupomService;
        this.emailTemplateService = emailTemplateService;
    }

    @Transactional(readOnly = true)
    public List<TransacaoResgate> extratoDoAluno(Long alunoId) {
        return resgateRepository.findByAlunoId(alunoId);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResgate> resgatesDaEmpresa(Long empresaId) {
        return resgateRepository.findByEmpresaId(empresaId);
    }

    @Transactional
    public TransacaoResgate resgatar(Long alunoId, Long vantagemId) {
        Aluno aluno = alunoRepository.findByIdForUpdate(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        Vantagem vantagem = vantagemRepository.findByIdWithEmpresa(vantagemId)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada"));
        aluno.debitar(vantagem.getCustoEmMoedas());
        String codigo = cupomService.gerarCodigoUnico();
        TransacaoResgate resgate = new TransacaoResgate(vantagem.getCustoEmMoedas(), codigo, aluno, vantagem);
        alunoRepository.save(aluno);
        resgate = resgateRepository.save(resgate);
        emailService.enviar(aluno.getEmail(), "Moeda Estudantil — cupom de resgate",
                emailTemplateService.cupomParaAluno(aluno.getNome(), vantagem, codigo));
        emailService.enviar(vantagem.getEmpresa().getEmail(), "Moeda Estudantil — cupom para conferência",
                emailTemplateService.cupomParaEmpresa(aluno, vantagem, codigo));
        return resgate;
    }
}
