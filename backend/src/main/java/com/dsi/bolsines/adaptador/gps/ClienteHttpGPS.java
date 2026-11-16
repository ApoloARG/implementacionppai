package com.dsi.bolsines.adaptador.gps;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utilidad de bajo nivel que usan los servicios GPS (Adaptables) para hacer el
 * GET contra la API REST del dispositivo. No forma parte del patron: es solo la
 * forma tecnica de "hablar" con el GPS Tracker.
 *
 * Devuelve el cuerpo de la respuesta o null si el GPS no pudo informar
 * (bolsin inexistente, codigo de CM que no coincide, API Key invalida o API caida).
 */
final class ClienteHttpGPS {

    private static final Logger LOG = Logger.getLogger(ClienteHttpGPS.class.getName());
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private ClienteHttpGPS() {
    }

    static String get(String urlBase, String ruta, String apiKey, Map<String, Object> parametros) {
        StringBuilder url = new StringBuilder(urlBase).append(ruta).append('?');
        parametros.forEach((k, v) -> url.append(k).append('=')
                .append(URLEncoder.encode(String.valueOf(v), StandardCharsets.UTF_8)).append('&'));
        HttpRequest request = HttpRequest.newBuilder(URI.create(url.substring(0, url.length() - 1)))
                .header("X-API-Key", apiKey)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
            LOG.warning("GPS Tracker respondio " + response.statusCode() + " para " + url + " -> " + response.body());
            return null;
        } catch (IOException e) {
            LOG.warning("No se pudo conectar con el GPS Tracker (" + urlBase + "): " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    static Map<String, Object> params(Object... claveValor) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < claveValor.length; i += 2) {
            m.put((String) claveValor[i], claveValor[i + 1]);
        }
        return m;
    }
}
