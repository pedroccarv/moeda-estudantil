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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final NotificacaoEmailRepository repository;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remetente;

    public EmailService(NotificacaoEmailRepository repository,
                        ObjectProvider<JavaMailSender> mailSender,
                        @Value("${spring.mail.username:}") String remetente) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Transactional
    public NotificacaoEmail enviar(String destinatario, String assunto, String corpo) {
        NotificacaoEmail email = new NotificacaoEmail(destinatario, assunto, corpo);
        email.enviar();
        email = repository.save(email);
        dispararSmtp(email);
        return email;
    }

    @Transactional(readOnly = true)
    public List<NotificacaoEmail> listarPorDestinatario(String destinatario) {
        return repository.findByDestinatarioOrderByDataEnvioDesc(destinatario);
    }

    private void dispararSmtp(NotificacaoEmail email) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.info("E-mail registrado para {} (sem SMTP): {}", email.getDestinatario(), email.getAssunto());
            return;
        }
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(email.getDestinatario());
            mensagem.setSubject(email.getAssunto());
            mensagem.setText(email.getCorpo());
            if (remetente != null && !remetente.isBlank()) {
                mensagem.setFrom(remetente);
            }
            sender.send(mensagem);
            log.info("E-mail SMTP enviado para {}", email.getDestinatario());
        } catch (RuntimeException ex) {
            log.warn("Falha no SMTP para {}: {}", email.getDestinatario(), ex.getMessage());
        }
    }
}
