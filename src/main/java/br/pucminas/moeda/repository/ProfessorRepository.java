package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.Professor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Professor p where p.id = :id")
    Optional<Professor> findByIdForUpdate(Long id);

    @Query("select p.id from Professor p order by p.id")
    List<Long> findAllIds();

    @Query("select p from Professor p join fetch p.instituicao where p.id = :id")
    Optional<Professor> findByIdWithInstituicao(Long id);
}
