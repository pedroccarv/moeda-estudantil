package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.NotificacaoEmail;
import br.pucminas.moeda.repository.NotificacaoEmailRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EmailService {
    public record EmailRegistrado(Long id) {}
    private final NotificacaoEmailRepository repository;
    private final ApplicationEventPublisher events;

    public EmailService(NotificacaoEmailRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public NotificacaoEmail enviar(String destinatario, String assunto, String corpo) {
        NotificacaoEmail email = new NotificacaoEmail(destinatario, assunto, corpo);
        email.registrar();
        email = repository.save(email);
        events.publishEvent(new EmailRegistrado(email.getId()));
        return email;
    }

    @Transactional(readOnly = true)
    public List<NotificacaoEmail> listarPorDestinatario(String destinatario) {
        return repository.findByDestinatarioOrderByDataEnvioDesc(destinatario);
    }
}
