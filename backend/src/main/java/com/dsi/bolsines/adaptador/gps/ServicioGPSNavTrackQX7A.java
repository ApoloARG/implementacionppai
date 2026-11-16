package com.dsi.bolsines.adaptador.gps;

/**
 * PATRON ADAPTER - Adaptable (servicio externo) del GPS Tracker modelo NavTrack QX-7A.
 * Recibe la CM DESTINO y devuelve un unico String con los items separados por comas:
 * "numeroBolsin,latitud,longitud,fechaHora".
 */
public class ServicioGPSNavTrackQX7A {

    private final String urlBase;

    public ServicioGPSNavTrackQX7A(String urlBase) {
        this.urlBase = urlBase;
    }

    /** GET /api/navtrack-qx-7a/retrieveTrackingData -> "101,-31.4201,-64.1888,2026-03-07T12:30" */
    public String retrieveTrackingData(String apiKey, int numeroBolsin, String codigoCMDestino) {
        return ClienteHttpGPS.get(urlBase, "/api/navtrack-qx-7a/retrieveTrackingData", apiKey,
                ClienteHttpGPS.params("numeroBolsin", numeroBolsin,
                        "codigoComisionMedicaDestino", codigoCMDestino));
    }
}
