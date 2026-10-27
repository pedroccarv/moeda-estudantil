package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.repository.EmpresaParceiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaParceiraService {

    private final EmpresaParceiraRepository empresaRepository;

    public EmpresaParceiraService(EmpresaParceiraRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpresaParceira> listar() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EmpresaParceira buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + id));
    }

    @Transactional
    public EmpresaParceira salvar(EmpresaParceira empresa) {
        if (empresa.getId() == null) {
            if (empresaRepository.existsByEmail(empresa.getEmail())) {
                throw new IllegalArgumentException("E-mail já cadastrado");
            }
        } else {
            EmpresaParceira existente = buscarPorId(empresa.getId());
            if (!existente.getEmail().equals(empresa.getEmail())
                    && empresaRepository.existsByEmail(empresa.getEmail())) {
                throw new IllegalArgumentException("E-mail já cadastrado");
            }
            if (empresa.getSenha() == null || empresa.getSenha().isBlank()) {
                empresa.setSenha(existente.getSenha());
            }
        }
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void excluir(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new IllegalArgumentException("Empresa não encontrada: " + id);
        }
        empresaRepository.deleteById(id);
    }
}
