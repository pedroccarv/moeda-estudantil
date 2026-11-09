package br.pucminas.moeda.domain;

import br.pucminas.moeda.service.PeriodoLetivo;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PeriodoLetivoTest {
    @Test void fronteirasDosSemestres() {
        assertEquals("2026-1", PeriodoLetivo.de(LocalDate.of(2026, 6, 30)));
        assertEquals("2026-2", PeriodoLetivo.de(LocalDate.of(2026, 7, 1)));
        assertEquals(1, PeriodoLetivo.decorridos("2026-2", "2027-1"));
        assertEquals(2, PeriodoLetivo.decorridos("2025-2", "2026-2"));
        assertEquals(0, PeriodoLetivo.decorridos("2026-2", "2026-2"));
        assertEquals(-1, PeriodoLetivo.decorridos("2027-1", "2026-2"));
        assertThrows(IllegalArgumentException.class, () -> PeriodoLetivo.decorridos("2026-3", "2027-1"));
    }
}
