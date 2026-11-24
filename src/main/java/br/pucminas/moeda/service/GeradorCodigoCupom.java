package br.pucminas.moeda.service;

import br.pucminas.moeda.repository.TransacaoResgateRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Pure Fabrication (GRASP): isola a geração e verificação de unicidade
 * de códigos de cupom, aumentando a coesão do ResgateService e protegendo-o
 * contra variações no formato ou algoritmo de geração.
 */
@Component
public class GeradorCodigoCupom {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 8;
    private static final int MAX_TENTATIVAS = 10;

    private final TransacaoResgateRepository resgateRepository;
    private final SecureRandom random = new SecureRandom();

    public GeradorCodigoCupom(TransacaoResgateRepository resgateRepository) {
        this.resgateRepository = resgateRepository;
    }

    public String gerarUnico() {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS; tentativa++) {
            String candidato = gerarAleatorio();
            if (!resgateRepository.existsByCodigoCupom(candidato)) {
                return candidato;
            }
        }
        throw new IllegalStateException("Não foi possível gerar o código do cupom");
    }

    private String gerarAleatorio() {
        StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            codigo.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return codigo.toString();
    }
}
