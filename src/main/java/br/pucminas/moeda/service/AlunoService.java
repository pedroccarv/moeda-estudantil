package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.InstituicaoEnsino;
import br.pucminas.moeda.repository.AlunoRepository;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final InstituicaoEnsinoRepository instituicaoRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        InstituicaoEnsinoRepository instituicaoRepository) {
        this.alunoRepository = alunoRepository;
        this.instituicaoRepository = instituicaoRepository;
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

    @Transactional
    public Aluno salvar(Aluno aluno, Long instituicaoId) {
        InstituicaoEnsino instituicao = instituicaoRepository.findById(instituicaoId)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));

        if (aluno.getId() == null) {
            if (alunoRepository.existsByEmail(aluno.getEmail())) {
                throw new IllegalArgumentException("E-mail já cadastrado");
            }
            if (alunoRepository.existsByCpf(aluno.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
            aluno.setSaldoMoedas(0);
        } else {
            Aluno existente = buscarPorId(aluno.getId());
            if (!existente.getEmail().equals(aluno.getEmail())
                    && alunoRepository.existsByEmail(aluno.getEmail())) {
                throw new IllegalArgumentException("E-mail já cadastrado");
            }
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
