package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    @Query("select p from Professor p join fetch p.instituicao where p.id = :id")
    Optional<Professor> findByIdWithInstituicao(Long id);
}
