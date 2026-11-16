package com.dsi.bolsines.adaptador.gps;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON ADAPTER - Adaptador concreto para el GPS Tracker GeoPulse MTR-900.
 * Traduce la matriz de valores sin tipo que devuelve el dispositivo al formato comun.
 */
public class AdaptadorGPSGeoPulseMTR900 implements IAdaptadorGPSTracker {

    private final String claveAPI;
    private final int[] numerosBolsin;
    private Object[][][] respuestas;

    // Composicion: el adaptador envuelve al servicio externo (Adaptable)
    private final ServicioGPSGeoPulseMTR900 servicioGPS;

    public AdaptadorGPSGeoPulseMTR900(String urlBase, String claveAPI, int[] numerosBolsin) {
        this.claveAPI = claveAPI;
        this.numerosBolsin = numerosBolsin;
        this.servicioGPS = new ServicioGPSGeoPulseMTR900(urlBase);
    }

    @Override
    public Object[][] recibirDatosLocalizacion() {
        solicitarDatosLocalizacion();
        return adaptarDatosLocalizacion();
    }

    @Override
    public void solicitarDatosLocalizacion() {
        respuestas = new Object[numerosBolsin.length][][];
        for (int i = 0; i < numerosBolsin.length; i++) {
            respuestas[i] = servicioGPS.fetchCargoPositions(claveAPI, numerosBolsin[i]);
        }
    }

    @Override
    public Object[][] adaptarDatosLocalizacion() {
        List<Object[]> datos = new ArrayList<>();
        for (Object[][] matriz : respuestas) {
            if (matriz != null) {
                for (Object[] fila : matriz) {
                    Object[] convertida = convertirFila(fila);
                    if (convertida != null) {
                        datos.add(convertida);
                    }
                }
            }
        }
        return datos.toArray(new Object[0][]);
    }

    /** Metodo propio de este adaptador: [101, -31.4201, -64.1888, "2026-03-07T12:30:00"]. */
    private Object[] convertirFila(Object[] fila) {
        try {
            return new Object[] {
                    ((Number) fila[0]).intValue(),
                    ((Number) fila[1]).doubleValue(),
                    ((Number) fila[2]).doubleValue(),
                    LocalDateTime.parse(String.valueOf(fila[3]))
            };
        } catch (RuntimeException e) {
            return null;
        }
    }
}
