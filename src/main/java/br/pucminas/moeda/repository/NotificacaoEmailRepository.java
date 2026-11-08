package br.pucminas.moeda.repository;

import br.pucminas.moeda.domain.NotificacaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoEmailRepository extends JpaRepository<NotificacaoEmail, Long> {

    List<NotificacaoEmail> findByDestinatarioOrderByDataEnvioDesc(String destinatario);
}
