package br.pucminas.moeda.service;

import java.time.LocalDate;
import java.time.ZoneId;

public final class PeriodoLetivo {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private PeriodoLetivo() {
    }

    public static String atual() {
        return de(LocalDate.now(ZONA));
    }

    public static String de(LocalDate data) {
        return data.getYear() + "-" + (data.getMonthValue() <= 6 ? 1 : 2);
    }

    public static int decorridos(String anterior, String atual) {
        return indice(atual) - indice(anterior);
    }

    private static int indice(String periodo) {
        if (periodo == null || !periodo.matches("\\d{4}-[12]")) {
            throw new IllegalArgumentException("Período letivo inválido: " + periodo);
        }
        return Integer.parseInt(periodo.substring(0, 4)) * 2 + Integer.parseInt(periodo.substring(5));
    }

    public static String rotulo() {
        return atual().replace('-', '/');
    }
}
