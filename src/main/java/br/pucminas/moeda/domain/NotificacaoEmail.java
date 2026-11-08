package br.pucminas.moeda.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao_email")
public class NotificacaoEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(nullable = false, length = 150)
    private String assunto;

    @Column(nullable = false, length = 2000)
    private String corpo;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    public NotificacaoEmail() {
    }

    public NotificacaoEmail(String destinatario, String assunto, String corpo) {
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    public void enviar() {
        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException("Destinatário é obrigatório");
        }
        if (assunto == null || assunto.isBlank()) {
            throw new IllegalArgumentException("Assunto é obrigatório");
        }
        if (corpo == null || corpo.isBlank()) {
            throw new IllegalArgumentException("Corpo do e-mail é obrigatório");
        }
        this.dataEnvio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
