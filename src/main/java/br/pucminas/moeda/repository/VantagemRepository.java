package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.Vantagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {

    @Query("select v from Vantagem v join fetch v.empresa order by v.custoEmMoedas, v.descricao")
    List<Vantagem> findAllWithEmpresa();

    @Query("select v from Vantagem v join fetch v.empresa where v.empresa.id = :empresaId order by v.descricao")
    List<Vantagem> findByEmpresaId(Long empresaId);

    @Query("select v from Vantagem v join fetch v.empresa where v.id = :id")
    Optional<Vantagem> findByIdWithEmpresa(Long id);
}
