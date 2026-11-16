package com.dsi.bolsines.adaptador.gps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PATRON ADAPTER - Adaptable (servicio externo) del GPS Tracker modelo GeoPulse MTR-900.
 * Recibe solo el numero de bolsin y devuelve una MATRIZ:
 * [[numeroBolsin, latitud, longitud, fechaHora]].
 */
public class ServicioGPSGeoPulseMTR900 {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String urlBase;

    public ServicioGPSGeoPulseMTR900(String urlBase) {
        this.urlBase = urlBase;
    }

    /** GET /api/geopulse-mtr-900/fetchCargoPositions -> [[101,-31.4201,-64.1888,"2026-03-07T12:30:00"]] */
    public Object[][] fetchCargoPositions(String apiKey, int numeroBolsin) {
        String cuerpo = ClienteHttpGPS.get(urlBase, "/api/geopulse-mtr-900/fetchCargoPositions", apiKey,
                ClienteHttpGPS.params("numeroBolsin", numeroBolsin));
        if (cuerpo == null) {
            return null;
        }
        try {
            return MAPPER.readValue(cuerpo, Object[][].class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
