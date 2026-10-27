package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.InstituicaoEnsino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituicaoEnsinoRepository extends JpaRepository<InstituicaoEnsino, Long> {
    boolean existsByNome(String nome);
}
