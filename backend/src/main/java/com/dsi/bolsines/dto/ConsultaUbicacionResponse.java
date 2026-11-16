package com.dsi.bolsines.dto;

import java.util.List;

public record ConsultaUbicacionResponse(
        String empleadoLogueado,
        String codigoCMUsuario,
        String nombreCMUsuario,
        String modeloGPS,
        List<BolsinUbicadoResponse> bolsines,
        List<BolsinSinUbicacionResponse> bolsinesSinUbicacion,
        MapaResponse mapa) {
}
