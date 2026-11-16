package com.dsi.bolsines.adaptador.gps;

/**
 * PATRON ADAPTER - Adaptable (servicio externo) del GPS Tracker modelo XTR-4500L.
 * Representa la interfaz propia del dispositivo, incompatible con la del sistema:
 * recibe la CM ORIGEN y devuelve un JSON en formato cadena de caracteres.
 */
public class ServicioGPSXTR4500L {

    private final String urlBase;

    public ServicioGPSXTR4500L(String urlBase) {
        this.urlBase = urlBase;
    }

    /** GET /api/xtr-4500l/getBolsinLocation -> {"items":[{numeroBolsin, latitud, longitud, fechaHoraUltimaActualizacion}]} */
    public String getBolsinLocation(String apiKey, int numeroBolsin, String codigoCMOrigen) {
        return ClienteHttpGPS.get(urlBase, "/api/xtr-4500l/getBolsinLocation", apiKey,
                ClienteHttpGPS.params("numeroBolsin", numeroBolsin,
                        "codigoComisionMedicaOrigen", codigoCMOrigen));
    }
}
