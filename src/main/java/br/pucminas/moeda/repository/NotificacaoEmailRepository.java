package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.NotificacaoEmail;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificacaoEmailRepository extends JpaRepository<NotificacaoEmail, Long> {

    List<NotificacaoEmail> findByDestinatarioOrderByDataEnvioDesc(String destinatario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from NotificacaoEmail e where e.id = :id")
    Optional<NotificacaoEmail> findByIdForUpdate(Long id);

    @Query("select e.id from NotificacaoEmail e where e.status in :status and e.tentativas < :maximo order by e.id")
    List<Long> findIdsParaEnvio(List<NotificacaoEmail.Status> status, int maximo, Pageable pagina);
}
