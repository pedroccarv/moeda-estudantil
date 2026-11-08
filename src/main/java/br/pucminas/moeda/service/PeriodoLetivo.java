package br.pucminas.moeda.service;

import java.time.LocalDate;
import java.time.ZoneId;

public final class PeriodoLetivo {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private PeriodoLetivo() {
    }

    public static String atual() {
        LocalDate hoje = LocalDate.now(ZONA);
        int semestre = hoje.getMonthValue() <= 6 ? 1 : 2;
        return hoje.getYear() + "-" + semestre;
    }

    public static String rotulo() {
        return atual().replace('-', '/');
    }
}
