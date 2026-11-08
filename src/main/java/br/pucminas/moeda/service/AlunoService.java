package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.InstituicaoEnsino;
import br.pucminas.moeda.domain.TransacaoEnvio;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import br.pucminas.moeda.repository.TransacaoEnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final InstituicaoEnsinoRepository instituicaoRepository;
    private final TransacaoEnvioRepository envioRepository;
    private final AuthService authService;

    public AlunoService(AlunoRepository alunoRepository,
                        InstituicaoEnsinoRepository instituicaoRepository,
                        TransacaoEnvioRepository envioRepository,
                        AuthService authService) {
        this.alunoRepository = alunoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.envioRepository = envioRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<Aluno> listar() {
        return alunoRepository.findAllWithInstituicao();
    }

    @Transactional(readOnly = true)
    public Aluno buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + id));
        // força carregamento da instituição dentro da transação
        aluno.getInstituicao().getNome();
        return aluno;
    }

    @Transactional(readOnly = true)
    public List<TransacaoEnvio> recebimentos(Long alunoId) {
        return envioRepository.findByAlunoId(alunoId);
    }

    @Transactional
    public Aluno salvar(Aluno aluno, Long instituicaoId) {
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (aluno.getEmail() == null || aluno.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }
        if (aluno.getCpf() == null || aluno.getCpf().isBlank()
                || aluno.getRg() == null || aluno.getRg().isBlank()
                || aluno.getEndereco() == null || aluno.getEndereco().isBlank()
                || aluno.getCurso() == null || aluno.getCurso().isBlank()) {
            throw new IllegalArgumentException("Preencha CPF, RG, endereço e curso");
        }
        InstituicaoEnsino instituicao = instituicaoRepository.findById(instituicaoId)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));

        aluno.setNome(aluno.getNome().trim());
        aluno.setEmail(aluno.getEmail().trim().toLowerCase());
        aluno.setCpf(aluno.getCpf().trim());
        aluno.setRg(aluno.getRg().trim());
        aluno.setEndereco(aluno.getEndereco().trim());
        aluno.setCurso(aluno.getCurso().trim());

        if (aluno.getId() == null) {
            if (aluno.getSenha() == null || aluno.getSenha().isBlank()) {
                throw new IllegalArgumentException("Senha é obrigatória");
            }
            authService.validarEmailDisponivel(aluno.getEmail(), null);
            if (alunoRepository.existsByCpf(aluno.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
            aluno.setSaldoMoedas(0);
        } else {
            Aluno existente = buscarPorId(aluno.getId());
            authService.validarEmailDisponivel(aluno.getEmail(), existente.getId());
            if (!existente.getCpf().equals(aluno.getCpf())
                    && alunoRepository.existsByCpf(aluno.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
            aluno.setSaldoMoedas(existente.getSaldoMoedas());
            if (aluno.getSenha() == null || aluno.getSenha().isBlank()) {
                aluno.setSenha(existente.getSenha());
            }
        }

        aluno.setInstituicao(instituicao);
        return alunoRepository.save(aluno);
    }

    @Transactional
    public void excluir(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new IllegalArgumentException("Aluno não encontrado: " + id);
        }
        alunoRepository.deleteById(id);
    }
}
