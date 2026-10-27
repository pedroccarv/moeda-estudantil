package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query("select a from Aluno a join fetch a.instituicao")
    List<Aluno> findAllWithInstituicao();

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
