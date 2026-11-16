package com.dsi.bolsines.adaptador.gps;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PATRON ADAPTER - Adaptador concreto para el GPS Tracker XTR-4500L.
 * Traduce el JSON (en cadena de caracteres) que devuelve el dispositivo al
 * formato comun definido por IAdaptadorGPSTracker.
 */
public class AdaptadorGPSXTR4500L implements IAdaptadorGPSTracker {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String claveAPI;
    private final int[] numerosBolsin;
    private final String[] codigosCMOrigen;
    private String[] respuestas;

    // Composicion: el adaptador envuelve al servicio externo (Adaptable)
    private final ServicioGPSXTR4500L servicioGPS;

    public AdaptadorGPSXTR4500L(String urlBase, String claveAPI, int[] numerosBolsin, String[] codigosCMOrigen) {
        this.claveAPI = claveAPI;
        this.numerosBolsin = numerosBolsin;
        this.codigosCMOrigen = codigosCMOrigen;
        this.servicioGPS = new ServicioGPSXTR4500L(urlBase);
    }

    @Override
    public Object[][] recibirDatosLocalizacion() {
        solicitarDatosLocalizacion();
        return adaptarDatosLocalizacion();
    }

    @Override
    public void solicitarDatosLocalizacion() {
        respuestas = new String[numerosBolsin.length];
        for (int i = 0; i < numerosBolsin.length; i++) {
            respuestas[i] = servicioGPS.getBolsinLocation(claveAPI, numerosBolsin[i], codigosCMOrigen[i]);
        }
    }

    @Override
    public Object[][] adaptarDatosLocalizacion() {
        List<Object[]> datos = new ArrayList<>();
        for (String json : respuestas) {
            if (json != null) {
                for (Object[] fila : convertirJSON(json)) {
                    datos.add(fila);
                }
            }
        }
        return datos.toArray(new Object[0][]);
    }

    /** Metodo propio de este adaptador: parsea {"items":[{...}]}. */
    private Object[][] convertirJSON(String json) {
        try {
            JsonNode items = MAPPER.readTree(json).path("items");
            List<Object[]> filas = new ArrayList<>();
            for (JsonNode item : items) {
                filas.add(new Object[] {
                        item.get("numeroBolsin").asInt(),
                        item.get("latitud").asDouble(),
                        item.get("longitud").asDouble(),
                        LocalDateTime.parse(item.get("fechaHoraUltimaActualizacion").asText())
                });
            }
            return filas.toArray(new Object[0][]);
        } catch (JsonProcessingException | RuntimeException e) {
            return new Object[0][];
        }
    }
}
