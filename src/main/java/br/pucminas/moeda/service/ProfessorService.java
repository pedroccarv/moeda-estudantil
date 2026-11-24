package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.domain.TransacaoEnvio;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.ProfessorRepository;
import br.pucminas.moeda.repository.TransacaoEnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessorService {

    static final int MOEDAS_POR_SEMESTRE = 1000;

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TransacaoEnvioRepository envioRepository;
    private final EmailService emailService;
    private final FormatadorNotificacao formatadorNotificacao;

    public ProfessorService(ProfessorRepository professorRepository,
                            AlunoRepository alunoRepository,
                            TransacaoEnvioRepository envioRepository,
                            EmailService emailService,
                            FormatadorNotificacao formatadorNotificacao) {
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.envioRepository = envioRepository;
        this.emailService = emailService;
        this.formatadorNotificacao = formatadorNotificacao;
    }

    @Transactional
    public int creditarSemestreSePendente(Long professorId) {
        Professor professor = professorRepository.findByIdForUpdate(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        String periodo = PeriodoLetivo.atual();
        int semestres;
        if (professor.getUltimoPeriodoCreditado() != null) {
            semestres = PeriodoLetivo.decorridos(professor.getUltimoPeriodoCreditado(), periodo);
        } else {
            String ingresso = professor.getDataCadastro() == null ? periodo : PeriodoLetivo.de(professor.getDataCadastro());
            semestres = PeriodoLetivo.decorridos(ingresso, periodo) + 1;
        }
        if (semestres <= 0) {
            return 0;
        }
        int quantidade = Math.multiplyExact(semestres, MOEDAS_POR_SEMESTRE);
        professor.creditarSemestre(quantidade);
        professor.setUltimoPeriodoCreditado(periodo);
        professorRepository.save(professor);
        return quantidade;
    }

    @Transactional(readOnly = true)
    public Professor buscar(Long id) {
        Professor professor = professorRepository.findByIdWithInstituicao(id)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        professor.getInstituicao().getNome();
        return professor;
    }

    @Transactional(readOnly = true)
    public List<TransacaoEnvio> extrato(Long professorId) {
        return envioRepository.findByProfessorId(professorId);
    }

    @Transactional
    public TransacaoEnvio enviarMoedas(Long professorId, Long alunoId, Integer valor, String motivo) {
        // Ordem fixa: professor antes de aluno. Todas as alterações de saldo usam estes bloqueios.
        Professor professor = professorRepository.findByIdForUpdate(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        Aluno aluno = alunoRepository.findByIdForUpdate(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        aluno.getNome();
        TransacaoEnvio envio = professor.enviarMoedas(aluno, valor, motivo);
        professorRepository.save(professor);
        alunoRepository.save(aluno);
        envio = envioRepository.save(envio);
        FormatadorNotificacao.ConteudoEmail notificacao =
                formatadorNotificacao.recebimentoMoedas(professor, valor, envio.getMotivo());
        emailService.enviar(aluno.getEmail(), notificacao.assunto(), notificacao.corpo());
        return envio;
    }
}
