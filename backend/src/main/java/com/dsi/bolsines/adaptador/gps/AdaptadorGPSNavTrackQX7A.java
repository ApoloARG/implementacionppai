package com.dsi.bolsines.adaptador.gps;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON ADAPTER - Adaptador concreto para el GPS Tracker NavTrack QX-7A.
 * Traduce el String separado por comas que devuelve el dispositivo al formato comun.
 */
public class AdaptadorGPSNavTrackQX7A implements IAdaptadorGPSTracker {

    private final String claveAPI;
    private final int[] numerosBolsin;
    private final String[] codigosCMDestino;
    private String[] respuestas;

    // Composicion: el adaptador envuelve al servicio externo (Adaptable)
    private final ServicioGPSNavTrackQX7A servicioGPS;

    public AdaptadorGPSNavTrackQX7A(String urlBase, String claveAPI, int[] numerosBolsin, String[] codigosCMDestino) {
        this.claveAPI = claveAPI;
        this.numerosBolsin = numerosBolsin;
        this.codigosCMDestino = codigosCMDestino;
        this.servicioGPS = new ServicioGPSNavTrackQX7A(urlBase);
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
            respuestas[i] = servicioGPS.retrieveTrackingData(claveAPI, numerosBolsin[i], codigosCMDestino[i]);
        }
    }

    @Override
    public Object[][] adaptarDatosLocalizacion() {
        List<Object[]> datos = new ArrayList<>();
        for (String cadena : respuestas) {
            if (cadena != null) {
                Object[] fila = separarCadena(cadena);
                if (fila != null) {
                    datos.add(fila);
                }
            }
        }
        return datos.toArray(new Object[0][]);
    }

    /** Metodo propio de este adaptador: "101,-31.4201,-64.1888,2026-03-07T12:30". */
    private Object[] separarCadena(String cadena) {
        try {
            String[] partes = cadena.trim().split(",");
            return new Object[] {
                    Integer.parseInt(partes[0].trim()),
                    Double.parseDouble(partes[1].trim()),
                    Double.parseDouble(partes[2].trim()),
                    LocalDateTime.parse(partes[3].trim())
            };
        } catch (RuntimeException e) {
            return null;
        }
    }
}
