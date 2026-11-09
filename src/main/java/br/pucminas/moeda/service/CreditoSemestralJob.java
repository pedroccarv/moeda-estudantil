package br.pucminas.moeda.service;

import br.pucminas.moeda.repository.ProfessorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.jobs.enabled", havingValue = "true", matchIfMissing = true)
public class CreditoSemestralJob {
    private static final Logger log = LoggerFactory.getLogger(CreditoSemestralJob.class);
    private final ProfessorRepository repository;
    private final ProfessorService service;

    public CreditoSemestralJob(ProfessorRepository repository, ProfessorService service) {
        this.repository = repository;
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void creditarPendentes() {
        for (Long id : repository.findAllIds()) {
            try {
                service.creditarSemestreSePendente(id);
            } catch (RuntimeException ex) {
                log.error("Falha ao creditar semestre do professor {}", id, ex);
            }
        }
    }
}
