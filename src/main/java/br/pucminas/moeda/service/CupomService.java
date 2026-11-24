package br.pucminas.moeda.service;

import br.pucminas.moeda.repository.TransacaoResgateRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CupomService {
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 8;
    private static final int MAX_TENTATIVAS = 10;

    private final TransacaoResgateRepository resgateRepository;
    private final SecureRandom random = new SecureRandom();

    public CupomService(TransacaoResgateRepository resgateRepository) {
        this.resgateRepository = resgateRepository;
    }

    public String gerarCodigoUnico() {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS; tentativa++) {
            StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);
            for (int indice = 0; indice < TAMANHO_CODIGO; indice++) {
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
