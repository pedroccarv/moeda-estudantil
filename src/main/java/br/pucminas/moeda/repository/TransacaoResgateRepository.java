package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.TransacaoResgate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransacaoResgateRepository extends JpaRepository<TransacaoResgate, Long> {

    boolean existsByCodigoCupom(String codigoCupom);

    @Query("""
            select r from TransacaoResgate r
            join fetch r.aluno
            join fetch r.vantagem v
            join fetch v.empresa
            where r.aluno.id = :alunoId
            order by r.dataHora desc
            """)
    List<TransacaoResgate> findByAlunoId(Long alunoId);

    @Query("""
            select r from TransacaoResgate r
            join fetch r.aluno
            join fetch r.vantagem v
            join fetch v.empresa
            where v.empresa.id = :empresaId
            order by r.dataHora desc
            """)
    List<TransacaoResgate> findByEmpresaId(Long empresaId);
}
