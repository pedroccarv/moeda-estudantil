package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.TransacaoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransacaoEnvioRepository extends JpaRepository<TransacaoEnvio, Long> {

    @Query("""
            select t from TransacaoEnvio t
            join fetch t.professor
            join fetch t.aluno
            where t.professor.id = :professorId
            order by t.dataHora desc
            """)
    List<TransacaoEnvio> findByProfessorId(Long professorId);

    @Query("""
            select t from TransacaoEnvio t
            join fetch t.professor
            join fetch t.aluno
            where t.aluno.id = :alunoId
            order by t.dataHora desc
            """)
    List<TransacaoEnvio> findByAlunoId(Long alunoId);
}
