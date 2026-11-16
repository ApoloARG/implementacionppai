package com.dsi.bolsines.dto;

import java.util.List;

/** Mapa armado por InterfazGoogleMaps: centro, zoom, proveedor de teselas y marcadores. */
public record MapaResponse(
        Double centroLatitud,
        Double centroLongitud,
        Integer zoom,
        String urlTeselas,
        String atribucion,
        List<MarcadorMapaResponse> marcadores) {
}
