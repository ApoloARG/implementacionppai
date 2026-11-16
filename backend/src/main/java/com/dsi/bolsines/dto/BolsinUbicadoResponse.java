package com.dsi.bolsines.dto;

/** Bolsin enviado que el GPS Tracker pudo localizar (se muestra en el mapa). */
public record BolsinUbicadoResponse(
        Integer numeroBolsin,
        String numeroPrecinto,
        String codigoCMDestino,
        String nombreCMDestino,
        Double latitud,
        Double longitud,
        String fechaHoraUltimaActualizacion) {
}
