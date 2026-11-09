package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.NotificacaoEmail;
import br.pucminas.moeda.repository.NotificacaoEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.List;

@Component
public class EmailDispatcher {
    private static final Logger log = LoggerFactory.getLogger(EmailDispatcher.class);
    private final NotificacaoEmailRepository repository;
    private final EmailEntregaService entrega;
    private final int maxTentativas;

    public EmailDispatcher(NotificacaoEmailRepository repository, EmailEntregaService entrega,
                           @Value("${app.mail.max-tentativas:5}") int maxTentativas) {
        this.repository = repository;
        this.entrega = entrega;
        this.maxTentativas = maxTentativas;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aposConfirmacao(EmailService.EmailRegistrado evento) {
        tentar(evento.id());
    }

    @Scheduled(fixedDelayString = "${app.mail.retry-delay-ms:60000}", initialDelayString = "${app.mail.retry-delay-ms:60000}")
    public void reenviarPendentes() {
        for (Long id : repository.findIdsParaEnvio(
                List.of(NotificacaoEmail.Status.PENDENTE, NotificacaoEmail.Status.FALHA), maxTentativas, PageRequest.of(0, 50))) {
            tentar(id);
        }
    }

    private void tentar(Long id) {
        try {
            entrega.tentarEnviar(id);
        } catch (RuntimeException ex) {
            // A operação de moedas já foi confirmada; a fila permite recuperar o envio depois.
            log.error("Não foi possível processar a notificação {}", id, ex);
        }
    }
}
