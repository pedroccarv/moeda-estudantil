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
    private final GeradorCodigoCupom geradorCodigoCupom;
    private final FormatadorNotificacao formatadorNotificacao;

    public ResgateService(AlunoRepository alunoRepository,
                          VantagemRepository vantagemRepository,
                          TransacaoResgateRepository resgateRepository,
                          EmailService emailService,
                          GeradorCodigoCupom geradorCodigoCupom,
                          FormatadorNotificacao formatadorNotificacao) {
        this.alunoRepository = alunoRepository;
        this.vantagemRepository = vantagemRepository;
        this.resgateRepository = resgateRepository;
        this.emailService = emailService;
        this.geradorCodigoCupom = geradorCodigoCupom;
        this.formatadorNotificacao = formatadorNotificacao;
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
        String codigo = geradorCodigoCupom.gerarUnico();
        TransacaoResgate resgate = new TransacaoResgate(vantagem.getCustoEmMoedas(), codigo, aluno, vantagem);
        alunoRepository.save(aluno);
        resgate = resgateRepository.save(resgate);
        FormatadorNotificacao.ConteudoEmail emailAluno =
                formatadorNotificacao.cupomAluno(aluno.getNome(), vantagem, codigo);
        FormatadorNotificacao.ConteudoEmail emailEmpresa =
                formatadorNotificacao.cupomEmpresa(aluno, vantagem, codigo);
        emailService.enviar(aluno.getEmail(), emailAluno.assunto(), emailAluno.corpo());
        emailService.enviar(vantagem.getEmpresa().getEmail(), emailEmpresa.assunto(), emailEmpresa.corpo());
        return resgate;
    }
}
