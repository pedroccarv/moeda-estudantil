package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.TransacaoResgate;
import br.pucminas.moeda.domain.Vantagem;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.TransacaoResgateRepository;
import br.pucminas.moeda.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class ResgateService {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 8;

    private final AlunoRepository alunoRepository;
    private final VantagemRepository vantagemRepository;
    private final TransacaoResgateRepository resgateRepository;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public ResgateService(AlunoRepository alunoRepository,
                          VantagemRepository vantagemRepository,
                          TransacaoResgateRepository resgateRepository,
                          EmailService emailService) {
        this.alunoRepository = alunoRepository;
        this.vantagemRepository = vantagemRepository;
        this.resgateRepository = resgateRepository;
        this.emailService = emailService;
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
        String codigo = gerarCodigo();
        TransacaoResgate resgate = new TransacaoResgate(vantagem.getCustoEmMoedas(), codigo, aluno, vantagem);
        alunoRepository.save(aluno);
        resgate = resgateRepository.save(resgate);
        emailService.enviar(aluno.getEmail(), "Moeda Estudantil — cupom de resgate",
                corpoAluno(aluno.getNome(), vantagem, codigo));
        emailService.enviar(vantagem.getEmpresa().getEmail(), "Moeda Estudantil — cupom para conferência",
                corpoEmpresa(aluno, vantagem, codigo));
        return resgate;
    }

    private String corpoAluno(String nomeAluno, Vantagem vantagem, String codigo) {
        return "Olá, " + nomeAluno + ".\n\n"
                + "Seu resgate foi confirmado.\n\n"
                + "Vantagem: " + vantagem.getDescricao() + "\n"
                + "Empresa: " + vantagem.getEmpresa().getNome() + "\n"
                + "Custo: " + vantagem.getCustoEmMoedas() + " moedas\n"
                + "Código do cupom: " + codigo + "\n\n"
                + "Apresente este código na troca presencial.";
    }

    private String corpoEmpresa(Aluno aluno, Vantagem vantagem, String codigo) {
        return "Olá, " + vantagem.getEmpresa().getNome() + ".\n\n"
                + "O aluno " + aluno.getNome() + " (" + aluno.getEmail() + ") resgatou a vantagem \""
                + vantagem.getDescricao() + "\".\n\n"
                + "Código do cupom: " + codigo + "\n\n"
                + "Use o código para conferir a troca presencial.";
    }

    private String gerarCodigo() {
        for (int tentativa = 0; tentativa < 10; tentativa++) {
            StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);
            for (int i = 0; i < TAMANHO_CODIGO; i++) {
                codigo.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
            }
            String gerado = codigo.toString();
            if (!resgateRepository.existsByCodigoCupom(gerado)) {
                return gerado;
            }
        }
        throw new IllegalStateException("Não foi possível gerar o código do cupom");
    }
}
