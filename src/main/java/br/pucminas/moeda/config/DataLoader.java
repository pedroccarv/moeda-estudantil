package br.pucminas.moeda.config;

import br.pucminas.moeda.domain.InstituicaoEnsino;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final InstituicaoEnsinoRepository instituicaoRepository;

    public DataLoader(InstituicaoEnsinoRepository instituicaoRepository) {
        this.instituicaoRepository = instituicaoRepository;
    }

    @Override
    public void run(String... args) {
        criarSeNaoExistir("PUC Minas");
        criarSeNaoExistir("UFMG");
        criarSeNaoExistir("CEFET-MG");
        criarSeNaoExistir("UNA");
    }

    private void criarSeNaoExistir(String nome) {
        if (!instituicaoRepository.existsByNome(nome)) {
            instituicaoRepository.save(new InstituicaoEnsino(nome));
        }
    }
}
