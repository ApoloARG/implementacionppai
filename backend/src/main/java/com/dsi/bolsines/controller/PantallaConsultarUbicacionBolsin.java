package com.dsi.bolsines.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsi.bolsines.dto.ConfirmacionEnvioCorreoResponse;
import com.dsi.bolsines.dto.ConfirmarEnvioCorreoRequest;
import com.dsi.bolsines.dto.ConsultaUbicacionResponse;
import com.dsi.bolsines.dto.ErrorResponse;
import com.dsi.bolsines.dto.ResultadoResponse;
import com.dsi.bolsines.dto.SeleccionarBolsinRequest;
import com.dsi.bolsines.service.FlujoAlternativoException;
import com.dsi.bolsines.service.GestorConsultarUbicacionBolsin;

/**
 * Boundary del CU 36 (PantallaConsultarUbicacionBolsin del modelo).
 * Expone los eventos del actor como endpoints REST y delega todo en el Gestor.
 */
@RestController
@RequestMapping("/api/consultar-ubicacion-bolsin")
@CrossOrigin(origins = "*")
public class PantallaConsultarUbicacionBolsin {

    private final GestorConsultarUbicacionBolsin gestor;

    public PantallaConsultarUbicacionBolsin(GestorConsultarUbicacionBolsin gestor) {
        this.gestor = gestor;
    }

    /** Paso 1: el EB selecciona la opcion para consultar la ubicacion de los bolsines. */
    @PostMapping("/opcion")
    public ResponseEntity<ConsultaUbicacionResponse> opcConsultarUbicacionBolsin() {
        habilitarPantalla();
        return ResponseEntity.ok(gestor.opcConsultarUbicacionBolsin());
    }

    private void habilitarPantalla() {
        // En la API REST la pantalla se habilita en el front (React) al recibir la respuesta.
    }

    /** Paso 7: el EB selecciona un bolsin del mapa. */
    @PostMapping("/seleccionar-bolsin")
    public ResponseEntity<ConfirmacionEnvioCorreoResponse> tomarSeleccionBolsinEnMapa(
            @RequestBody SeleccionarBolsinRequest request) {
        return ResponseEntity.ok(gestor.tomarSeleccionBolsinEnMapa(request.numeroBolsin()));
    }

    /** Paso 9 (o A5): el EB confirma o rechaza el envio del correo. */
    @PostMapping("/confirmar-envio-correo")
    public ResponseEntity<ResultadoResponse> tomarConfirmacionEnvioCorreo(
            @RequestBody ConfirmarEnvioCorreoRequest request) {
        return ResponseEntity.ok(gestor.tomarConfirmacionEnvioCorreo(request.confirma()));
    }

    /** A4: el EB cancela la ejecucion del caso de uso. */
    @PostMapping("/cancelar")
    public ResponseEntity<Map<String, String>> tomarCancelacion() {
        gestor.cancelarCU();
        return ResponseEntity.ok(Map.of("mensaje", "Se canceló la consulta de seguimiento de bolsines."));
    }

    @ExceptionHandler(FlujoAlternativoException.class)
    public ResponseEntity<ErrorResponse> manejarFlujoAlternativo(FlujoAlternativoException ex) {
        HttpStatus status = "A1".equals(ex.getFlujo()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorResponse(ex.getFlujo(), ex.getMessage()));
    }
}
