package com.dsi.bolsines.service;

/** Representa un flujo alternativo del CU 36 que corta el flujo descripto. */
public class FlujoAlternativoException extends RuntimeException {

    private final String flujo;

    public FlujoAlternativoException(String flujo, String mensaje) {
        super(mensaje);
        this.flujo = flujo;
    }

    public String getFlujo() {
        return flujo;
    }
}
