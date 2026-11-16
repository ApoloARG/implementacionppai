package com.dsi.bolsines.dto;

public record ConfirmacionEnvioCorreoResponse(
        Integer numeroBolsin,
        String nombreCMDestino,
        String pregunta) {
}
