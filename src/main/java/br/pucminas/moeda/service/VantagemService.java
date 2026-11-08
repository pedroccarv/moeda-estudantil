package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.domain.Vantagem;
import br.pucminas.moeda.repository.EmpresaParceiraRepository;
import br.pucminas.moeda.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class VantagemService {

    private final VantagemRepository vantagemRepository;
    private final EmpresaParceiraRepository empresaRepository;
    private final FotoStorage fotoStorage;

    public VantagemService(VantagemRepository vantagemRepository,
                           EmpresaParceiraRepository empresaRepository,
                           FotoStorage fotoStorage) {
        this.vantagemRepository = vantagemRepository;
        this.empresaRepository = empresaRepository;
        this.fotoStorage = fotoStorage;
    }

    @Transactional(readOnly = true)
    public List<Vantagem> listarTodas() {
        return vantagemRepository.findAllWithEmpresa();
    }

    @Transactional(readOnly = true)
    public List<Vantagem> listarDaEmpresa(Long empresaId) {
        return vantagemRepository.findByEmpresaId(empresaId);
    }

    @Transactional(readOnly = true)
    public Vantagem buscarDaEmpresa(Long empresaId, Long vantagemId) {
        Vantagem vantagem = vantagemRepository.findByIdWithEmpresa(vantagemId)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada"));
        if (!vantagem.getEmpresa().getId().equals(empresaId)) {
            throw new IllegalArgumentException("Vantagem de outra empresa");
        }
        return vantagem;
    }

    @Transactional
    public Vantagem salvar(Long empresaId, Long vantagemId, String descricao, Integer custo, MultipartFile foto) {
        EmpresaParceira empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        Vantagem vantagem;
        if (vantagemId == null) {
            vantagem = new Vantagem();
        } else {
            vantagem = buscarDaEmpresa(empresaId, vantagemId);
        }
        vantagem.setDescricao(descricao);
        vantagem.setCustoEmMoedas(custo);
        String fotoUrl = fotoStorage.salvar(foto);
        if (fotoUrl != null) {
            vantagem.setFotoUrl(fotoUrl);
        }
        empresa.cadastrarVantagem(vantagem);
        return vantagemRepository.save(vantagem);
    }

    @Transactional
    public void excluir(Long empresaId, Long vantagemId) {
        Vantagem vantagem = buscarDaEmpresa(empresaId, vantagemId);
        vantagemRepository.delete(vantagem);
    }
}
