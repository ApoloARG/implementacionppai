package com.dsi.bolsines.boundary;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dsi.bolsines.dto.MapaResponse;
import com.dsi.bolsines.dto.MarcadorMapaResponse;

/**
 * Boundary hacia el servidor de mapas. Mantiene el nombre del modelo (InterfazGoogleMaps)
 * para ser consistente con la realizacion del CU; en la implementacion se usan las teselas
 * gratuitas de OpenStreetMap (dibujadas con Leaflet en el front) en lugar de la API paga de
 * Google Maps.
 */
@Component
public class InterfazGoogleMaps {

    private static final String URL_TESELAS = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
    private static final String ATRIBUCION = "&copy; OpenStreetMap contributors";
    // Centro de Argentina por si no hay ningun bolsin ubicado
    private static final double LAT_ARGENTINA = -34.0;
    private static final double LNG_ARGENTINA = -64.0;

    /**
     * Recibe los datos de localizacion ya adaptados (formato comun del Adapter)
     * y arma el mapa: centro, zoom y un marcador por bolsin.
     */
    public MapaResponse obtenerUbicacionMapa(Object[][] datosLocalizacion) {
        List<MarcadorMapaResponse> marcadores = new ArrayList<>();
        double minLat = 90, maxLat = -90, minLng = 180, maxLng = -180;
        for (Object[] fila : datosLocalizacion) {
            double lat = (Double) fila[1];
            double lng = (Double) fila[2];
            marcadores.add(new MarcadorMapaResponse((Integer) fila[0], lat, lng));
            minLat = Math.min(minLat, lat);
            maxLat = Math.max(maxLat, lat);
            minLng = Math.min(minLng, lng);
            maxLng = Math.max(maxLng, lng);
        }
        if (marcadores.isEmpty()) {
            return new MapaResponse(LAT_ARGENTINA, LNG_ARGENTINA, 4, URL_TESELAS, ATRIBUCION, marcadores);
        }
        double extension = Math.max(maxLat - minLat, maxLng - minLng);
        int zoom = extension < 0.05 ? 12 : extension < 1 ? 9 : extension < 5 ? 6 : extension < 15 ? 5 : 4;
        return new MapaResponse((minLat + maxLat) / 2, (minLng + maxLng) / 2, zoom,
                URL_TESELAS, ATRIBUCION, marcadores);
    }
}
