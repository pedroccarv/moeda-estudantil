package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.InstituicaoEnsino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstituicaoEnsinoRepository extends JpaRepository<InstituicaoEnsino, Long> {
    boolean existsByNome(String nome);

    Optional<InstituicaoEnsino> findByNome(String nome);
}
