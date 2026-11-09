package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.NotificacaoEmail;
import br.pucminas.moeda.repository.NotificacaoEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailEntregaService {
    private static final Logger log = LoggerFactory.getLogger(EmailEntregaService.class);
    private final NotificacaoEmailRepository repository;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remetente;
    private final boolean habilitado;
    private final int maxTentativas;

    public EmailEntregaService(NotificacaoEmailRepository repository, ObjectProvider<JavaMailSender> mailSender,
                               @Value("${app.mail.from:no-reply@moeda-estudantil.local}") String remetente,
                               @Value("${app.mail.enabled:true}") boolean habilitado,
                               @Value("${app.mail.max-tentativas:5}") int maxTentativas) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.habilitado = habilitado;
        this.maxTentativas = maxTentativas;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tentarEnviar(Long id) {
        if (!habilitado) return;
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) return;
        NotificacaoEmail email = repository.findByIdForUpdate(id).orElse(null);
        if (email == null || email.getStatus() == NotificacaoEmail.Status.ENVIADO
                || email.getStatus() == NotificacaoEmail.Status.LEGADO
                || email.getTentativas() >= maxTentativas) return;

        email.iniciarTentativa();
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(email.getDestinatario());
            mensagem.setSubject(email.getAssunto());
            mensagem.setText(email.getCorpo());
            sender.send(mensagem);
            email.marcarEnviado();
        } catch (RuntimeException ex) {
            email.marcarFalha();
            log.warn("Falha SMTP na notificação {} (tentativa {}): {}", id, email.getTentativas(), ex.getClass().getSimpleName());
        }
    }
}
