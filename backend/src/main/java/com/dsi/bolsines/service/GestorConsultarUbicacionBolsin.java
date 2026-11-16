package com.dsi.bolsines.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dsi.bolsines.adaptador.gps.AdaptadorGPSGeoPulseMTR900;
import com.dsi.bolsines.adaptador.gps.AdaptadorGPSNavTrackQX7A;
import com.dsi.bolsines.adaptador.gps.AdaptadorGPSXTR4500L;
import com.dsi.bolsines.adaptador.gps.IAdaptadorGPSTracker;
import com.dsi.bolsines.boundary.InterfazGoogleMaps;
import com.dsi.bolsines.dto.BolsinSinUbicacionResponse;
import com.dsi.bolsines.dto.BolsinUbicadoResponse;
import com.dsi.bolsines.dto.ConfirmacionEnvioCorreoResponse;
import com.dsi.bolsines.dto.ConsultaUbicacionResponse;
import com.dsi.bolsines.dto.MapaResponse;
import com.dsi.bolsines.dto.ResultadoResponse;
import com.dsi.bolsines.model.Bolsin;
import com.dsi.bolsines.model.ComisionMedica;
import com.dsi.bolsines.model.Empleado;
import com.dsi.bolsines.model.Sesion;
import com.dsi.bolsines.model.Usuario;
import com.dsi.bolsines.repository.IBolsinRepository;
import com.dsi.bolsines.repository.IEmpleadoRepository;
import com.dsi.bolsines.repository.ISesionRepository;

/**
 * Clase de control del CU 36 "Consultar seguimiento de bolsines".
 *
 * En el patron Adapter cumple el rol de CLIENTE: solo conoce a la interfaz
 * IAdaptadorGPSTracker. Al adaptador concreto lo conoce unicamente al crearlo
 * (crearAdaptadorGPS), segun el modelo de GPS configurado.
 *
 * Como se implementa con una API REST, los mensajes Gestor -> Pantalla del diagrama
 * (mostrarDatos..., solicitar..., informar...) se resuelven con el valor de retorno
 * de cada metodo (igual que en la implementacion de referencia de la catedra).
 */
@Service
public class GestorConsultarUbicacionBolsin {

    private final ISesionRepository sesionRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final IBolsinRepository bolsinRepository;
    private final InterfazGoogleMaps interfazGoogleMaps;
    private final GestorNotificarUbicacionBolsin gestorCU31;

    // ---- configuracion del GPS Tracker (application.properties) ----
    private final String modeloGPS;
    private final String urlServicioGPS;
    private final String claveAPIGPS; // API Key del modelo configurado

    // ---- estado del caso de uso ----
    private Empleado empleadoLogueado;
    private ComisionMedica cmUsuario;
    private List<Bolsin> bolsinEnviadoDeCM = new ArrayList<>();
    private int[] numerosBolsin;
    private String[] codigosCMOrigen;
    private String[] codigosCMDestino;
    private IAdaptadorGPSTracker adaptadorGPS;
    private Object[][] datosLocalizacionBolsin = new Object[0][];
    private Bolsin bolsinSeleccionado;
    private String emailGCM;

    public GestorConsultarUbicacionBolsin(
            ISesionRepository sesionRepository,
            IEmpleadoRepository empleadoRepository,
            IBolsinRepository bolsinRepository,
            InterfazGoogleMaps interfazGoogleMaps,
            GestorNotificarUbicacionBolsin gestorCU31,
            @Value("${gps.modelo}") String modeloGPS,
            @Value("${gps.url-base}") String urlServicioGPS,
            @Value("${gps.claves.xtr-4500l}") String claveXTR4500L,
            @Value("${gps.claves.navtrack-qx-7a}") String claveNavTrackQX7A,
            @Value("${gps.claves.geopulse-mtr-900}") String claveGeoPulseMTR900) {
        this.sesionRepository = sesionRepository;
        this.empleadoRepository = empleadoRepository;
        this.bolsinRepository = bolsinRepository;
        this.interfazGoogleMaps = interfazGoogleMaps;
        this.gestorCU31 = gestorCU31;
        this.modeloGPS = modeloGPS.toUpperCase();
        this.urlServicioGPS = urlServicioGPS;
        // Cada modelo de GPS Tracker tiene su propia API Key
        this.claveAPIGPS = switch (this.modeloGPS) {
            case "NAVTRACK-QX-7A" -> claveNavTrackQX7A;
            case "GEOPULSE-MTR-900" -> claveGeoPulseMTR900;
            default -> claveXTR4500L;
        };
    }

    // =====================================================================
    // Paso 1 a 6: el EB selecciona la opcion -> se muestran los bolsines en el mapa
    // =====================================================================
    public ConsultaUbicacionResponse opcConsultarUbicacionBolsin() {
        finCU(); // arranca limpio por si quedo un CU anterior a medio terminar
        validarEmpleadoLogueado();
        obtenerBolsinEnviadoDeCM();
        if (bolsinEnviadoDeCM.isEmpty()) {
            throw new FlujoAlternativoException("A1",
                    "No se encuentran bolsines en estado Enviado para la " + cmUsuario.getNombre() + ".");
        }
        buscarDatosDeLocalizacionBolsin();
        MapaResponse mapa = mapearBolsinEnMapa();
        return armarDatosLocalizacionBolsin(mapa);
    }

    private void validarEmpleadoLogueado() {
        Sesion actual = sesionRepository.findAll().stream()
                .filter(Sesion::sosActual)
                .max(Comparator.comparing(Sesion::getFechaHoraInicio))
                .orElseThrow(() -> new FlujoAlternativoException("ERROR", "No hay una sesión iniciada."));
        Usuario usuario = actual.getUsuario();
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.esTuUsuario(usuario)) {
                this.empleadoLogueado = empleado;
                this.cmUsuario = empleado.obtenerCM();
                return;
            }
        }
        throw new FlujoAlternativoException("ERROR", "El usuario logueado no corresponde a ningún empleado.");
    }

    private void obtenerBolsinEnviadoDeCM() {
        bolsinEnviadoDeCM = new ArrayList<>();
        for (Bolsin bolsin : bolsinRepository.findAll()) {
            if (bolsin.esTuCMOrigen(cmUsuario) && bolsin.sosEnviado()) {
                bolsinEnviadoDeCM.add(bolsin);
            }
        }
        bolsinEnviadoDeCM.sort(Comparator.comparing(Bolsin::getNumeroBolsin));
    }

    /** Arma los datos que necesita el GPS y se los pide a traves del Adapter. */
    private void buscarDatosDeLocalizacionBolsin() {
        int cantidad = bolsinEnviadoDeCM.size();
        numerosBolsin = new int[cantidad];
        codigosCMOrigen = new String[cantidad];
        codigosCMDestino = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            Bolsin bolsin = bolsinEnviadoDeCM.get(i);
            numerosBolsin[i] = bolsin.getNumeroBolsin();
            codigosCMOrigen[i] = bolsin.getCodigoCMOrigen();
            codigosCMDestino[i] = bolsin.getCodigoCMDestino();
        }
        crearAdaptadorGPS();
        // Polimorfismo: el Gestor no sabe que adaptador concreto esta usando
        datosLocalizacionBolsin = adaptadorGPS.recibirDatosLocalizacion();
    }

    /** Unico lugar donde el Gestor conoce a un adaptador concreto: al crearlo. */
    private void crearAdaptadorGPS() {
        switch (modeloGPS) {
            case "NAVTRACK-QX-7A":
                adaptadorGPS = new AdaptadorGPSNavTrackQX7A(urlServicioGPS, claveAPIGPS, numerosBolsin, codigosCMDestino);
                break;
            case "GEOPULSE-MTR-900":
                adaptadorGPS = new AdaptadorGPSGeoPulseMTR900(urlServicioGPS, claveAPIGPS, numerosBolsin);
                break;
            case "XTR-4500L":
            default:
                adaptadorGPS = new AdaptadorGPSXTR4500L(urlServicioGPS, claveAPIGPS, numerosBolsin, codigosCMOrigen);
                break;
        }
    }

    private MapaResponse mapearBolsinEnMapa() {
        return interfazGoogleMaps.obtenerUbicacionMapa(datosLocalizacionBolsin);
    }

    /** mostrarDatosLocalizacionBolsin(): lo que la pantalla tiene que mostrar. */
    private ConsultaUbicacionResponse armarDatosLocalizacionBolsin(MapaResponse mapa) {
        List<BolsinUbicadoResponse> ubicados = new ArrayList<>();
        Set<Integer> numerosUbicados = new HashSet<>();
        for (Object[] fila : datosLocalizacionBolsin) {
            Bolsin bolsin = buscarBolsinEnviado((Integer) fila[0]);
            if (bolsin == null || !numerosUbicados.add(bolsin.getNumeroBolsin())) {
                continue;
            }
            ubicados.add(new BolsinUbicadoResponse(
                    bolsin.getNumeroBolsin(),
                    bolsin.getNumeroPrecinto(),
                    bolsin.getCodigoCMDestino(),
                    bolsin.getCMDestino().getNombre(),
                    (Double) fila[1],
                    (Double) fila[2],
                    fila[3].toString()));
        }
        // A3: el GPS Tracker no pudo informar la ubicacion de estos bolsines
        List<BolsinSinUbicacionResponse> sinUbicacion = new ArrayList<>();
        for (Bolsin bolsin : bolsinEnviadoDeCM) {
            if (!numerosUbicados.contains(bolsin.getNumeroBolsin())) {
                sinUbicacion.add(new BolsinSinUbicacionResponse(
                        bolsin.getNumeroBolsin(), bolsin.getNumeroPrecinto(), bolsin.getCMDestino().getNombre()));
            }
        }
        return new ConsultaUbicacionResponse(
                empleadoLogueado.getApellido() + ", " + empleadoLogueado.getNombre(),
                cmUsuario.getCodigo(),
                cmUsuario.getNombre(),
                modeloGPS,
                ubicados,
                sinUbicacion,
                mapa);
    }

    // =====================================================================
    // Paso 7 y 8: el EB selecciona un bolsin del mapa
    // =====================================================================
    public ConfirmacionEnvioCorreoResponse tomarSeleccionBolsinEnMapa(Integer numeroBolsin) {
        Bolsin bolsin = numeroBolsin == null ? null : buscarBolsinEnviado(numeroBolsin);
        if (bolsin == null || buscarDatosDe(numeroBolsin) == null) {
            throw new FlujoAlternativoException("ERROR",
                    "El bolsín " + numeroBolsin + " no está ubicado en el mapa.");
        }
        this.bolsinSeleccionado = bolsin;
        return solicitarConfirmacionEnvioCorreo();
    }

    private ConfirmacionEnvioCorreoResponse solicitarConfirmacionEnvioCorreo() {
        String nombreCMDestino = bolsinSeleccionado.getCMDestino().getNombre();
        return new ConfirmacionEnvioCorreoResponse(
                bolsinSeleccionado.getNumeroBolsin(),
                nombreCMDestino,
                "¿Desea enviar un correo electrónico al Gerente de la " + nombreCMDestino
                        + " para informar la ubicación del bolsín " + bolsinSeleccionado.getNumeroBolsin() + "?");
    }

    // =====================================================================
    // Paso 9 a 11: el EB confirma (o no, A5) el envio del correo
    // =====================================================================
    public ResultadoResponse tomarConfirmacionEnvioCorreo(Boolean confirma) {
        if (bolsinSeleccionado == null) {
            throw new FlujoAlternativoException("ERROR", "Primero debe seleccionar un bolsín del mapa.");
        }
        if (!Boolean.TRUE.equals(confirma)) {
            // A5: el EB no selecciona la opcion para enviar el correo
            finCU();
            return new ResultadoResponse(false, "No se envió el correo electrónico (A5). Fin del caso de uso.", null);
        }
        obtenerGerenteDeCM();
        if (emailGCM == null) {
            throw new FlujoAlternativoException("ERROR",
                    "No se encontró el Gerente de la " + bolsinSeleccionado.getCMDestino().getNombre() + ".");
        }
        boolean exito = llamarCU31();
        String email = emailGCM;
        Integer numero = bolsinSeleccionado.getNumeroBolsin();
        finCU();
        return informarEjecucionExitosa(exito, numero, email);
    }

    private void obtenerGerenteDeCM() {
        ComisionMedica cmDestino = bolsinSeleccionado.getCMDestino();
        emailGCM = null;
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.sosDeCM(cmDestino) && empleado.sosGerente()) {
                emailGCM = empleado.getEmail();
                return;
            }
        }
    }

    /** <<include>> CU 31 Notificar ubicacion de bolsin. */
    private boolean llamarCU31() {
        Object[] datos = buscarDatosDe(bolsinSeleccionado.getNumeroBolsin());
        return gestorCU31.notificarUbicacionBolsin(
                emailGCM,
                (Integer) datos[0],
                (LocalDateTime) datos[3],
                (Double) datos[1],
                (Double) datos[2]);
    }

    private ResultadoResponse informarEjecucionExitosa(boolean exito, Integer numeroBolsin, String email) {
        if (!exito) {
            return new ResultadoResponse(false, "El CU 31 no pudo notificar la ubicación del bolsín.", email);
        }
        return new ResultadoResponse(true,
                "Se ejecutó con éxito el CU 31: se notificó al GCM (" + email + ") la ubicación del bolsín "
                        + numeroBolsin + ".", email);
    }

    // =====================================================================
    // A4: el EB cancela la ejecucion del caso de uso
    // =====================================================================
    public void cancelarCU() {
        finCU();
    }

    private void finCU() {
        empleadoLogueado = null;
        cmUsuario = null;
        bolsinEnviadoDeCM = new ArrayList<>();
        numerosBolsin = null;
        codigosCMOrigen = null;
        codigosCMDestino = null;
        adaptadorGPS = null;
        datosLocalizacionBolsin = new Object[0][];
        bolsinSeleccionado = null;
        emailGCM = null;
    }

    // ---- auxiliares ----
    private Bolsin buscarBolsinEnviado(Integer numeroBolsin) {
        for (Bolsin bolsin : bolsinEnviadoDeCM) {
            if (bolsin.getNumeroBolsin().equals(numeroBolsin)) {
                return bolsin;
            }
        }
        return null;
    }

    private Object[] buscarDatosDe(Integer numeroBolsin) {
        for (Object[] fila : datosLocalizacionBolsin) {
            if (numeroBolsin.equals(fila[0])) {
                return fila;
            }
        }
        return null;
    }
}
