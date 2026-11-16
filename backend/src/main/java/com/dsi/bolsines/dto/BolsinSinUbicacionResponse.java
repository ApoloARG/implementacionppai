package com.dsi.bolsines.dto;

/** Bolsin enviado del que el GPS Tracker no pudo informar la ubicacion (flujo A3). */
public record BolsinSinUbicacionResponse(
        Integer numeroBolsin,
        String numeroPrecinto,
        String nombreCMDestino) {
}
