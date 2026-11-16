package com.dsi.bolsines.service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

/**
 * CU 31 "Notificar ubicacion de bolsin" (INCLUIDO por el CU 36).
 *
 * El envio del correo es responsabilidad de este CU, no del CU 36: el CU 36 solo
 * le pasa el email del GCM destino y los datos del bolsin. Como el CU 31 no es el
 * asignado al grupo, su ejecucion se SIMULA registrando el correo en el log.
 */
@Service
public class GestorNotificarUbicacionBolsin {

    private static final Logger LOG = Logger.getLogger(GestorNotificarUbicacionBolsin.class.getName());

    public boolean notificarUbicacionBolsin(String emailGCM, Integer numeroBolsin,
                                            LocalDateTime fechaHoraUltimaActualizacion,
                                            Double latitud, Double longitud) {
        LOG.info(String.format(
                "[CU 31 - simulado] Correo a %s: bolsin %d, ultima actualizacion %s, coordenadas (%.4f, %.4f)",
                emailGCM, numeroBolsin, fechaHoraUltimaActualizacion, latitud, longitud));
        return true;
    }
}
