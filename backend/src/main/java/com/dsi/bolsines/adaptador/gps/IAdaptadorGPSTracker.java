package com.dsi.bolsines.adaptador.gps;

/**
 * PATRON ADAPTER - Interfaz Objetivo (Target).
 *
 * Define la firma comun que el Cliente (GestorConsultarUbicacionBolsin) usa para
 * obtener la localizacion de los bolsines, sin conocer que modelo de GPS Tracker
 * hay del otro lado ni el formato en que responde.
 *
 * Cada fila del Object[][] que se devuelve tiene SIEMPRE el mismo formato:
 *   [0] Integer        numeroBolsin
 *   [1] Double         latitud
 *   [2] Double         longitud
 *   [3] LocalDateTime  fechaHoraUltimaActualizacion
 */
public interface IAdaptadorGPSTracker {

    /** Metodo que llama el Cliente: dispara la solicitud y la adaptacion. */
    Object[][] recibirDatosLocalizacion();

    /** Le pide los datos al servicio externo (Adaptable). */
    void solicitarDatosLocalizacion();

    /** Convierte la respuesta propia del modelo al formato comun. */
    Object[][] adaptarDatosLocalizacion();
}
