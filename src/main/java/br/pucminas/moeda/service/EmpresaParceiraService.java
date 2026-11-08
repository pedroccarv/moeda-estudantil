package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.repository.EmpresaParceiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaParceiraService {

    private final EmpresaParceiraRepository empresaRepository;
    private final AuthService authService;

    public EmpresaParceiraService(EmpresaParceiraRepository empresaRepository, AuthService authService) {
        this.empresaRepository = empresaRepository;
        this.authService = authService;
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
        if (empresa.getNome() == null || empresa.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (empresa.getEmail() == null || empresa.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }
        empresa.setNome(empresa.getNome().trim());
        empresa.setEmail(empresa.getEmail().trim().toLowerCase());
        if (empresa.getCnpj() != null) {
            empresa.setCnpj(empresa.getCnpj().trim());
        }
        if (empresa.getId() == null) {
            if (empresa.getSenha() == null || empresa.getSenha().isBlank()) {
                throw new IllegalArgumentException("Senha é obrigatória");
            }
            authService.validarEmailDisponivel(empresa.getEmail(), null);
        } else {
            EmpresaParceira existente = buscarPorId(empresa.getId());
            authService.validarEmailDisponivel(empresa.getEmail(), existente.getId());
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
