package com.dsi.bolsines.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsi.bolsines.dto.IniciarSesionRequest;
import com.dsi.bolsines.dto.UsuarioDemoResponse;
import com.dsi.bolsines.model.Empleado;
import com.dsi.bolsines.model.Sesion;
import com.dsi.bolsines.model.Usuario;
import com.dsi.bolsines.repository.IEmpleadoRepository;
import com.dsi.bolsines.repository.ISesionRepository;
import com.dsi.bolsines.repository.IUsuarioRepository;

/**
 * SOPORTE PARA LA DEMO (no forma parte del CU 36).
 * Permite cambiar el usuario logueado para mostrar los flujos alternativos en la defensa
 * (por ejemplo, un EB de una CM sin bolsines enviados -> A1).
 */
@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
public class SesionDemoController {

    private final IUsuarioRepository usuarioRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final ISesionRepository sesionRepository;

    public SesionDemoController(IUsuarioRepository usuarioRepository, IEmpleadoRepository empleadoRepository,
                                ISesionRepository sesionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.sesionRepository = sesionRepository;
    }

    @GetMapping("/usuarios")
    public List<UsuarioDemoResponse> listarUsuarios() {
        Sesion actual = sesionRepository.findAll().stream().filter(Sesion::sosActual).findFirst().orElse(null);
        List<UsuarioDemoResponse> usuarios = new ArrayList<>();
        for (Empleado e : empleadoRepository.findAll()) {
            if (e.getUsuario() == null) {
                continue;
            }
            boolean activa = actual != null && e.esTuUsuario(actual.getUsuario());
            usuarios.add(new UsuarioDemoResponse(e.getUsuario().getNombre(),
                    e.getApellido() + ", " + e.getNombre(),
                    e.sosGerente() ? "Gerente de CM" : "Encargado de Bolsines",
                    e.obtenerCM().getNombre(), activa));
        }
        return usuarios;
    }

    @PostMapping("/sesion")
    public ResponseEntity<Map<String, String>> iniciarSesion(@RequestBody IniciarSesionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.nombreUsuario()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Usuario inexistente"));
        }
        LocalDateTime ahora = LocalDateTime.now();
        for (Sesion s : sesionRepository.findAll()) {
            if (s.sosActual()) {
                s.cerrar(ahora);
                sesionRepository.save(s);
            }
        }
        sesionRepository.save(new Sesion(ahora, usuario));
        return ResponseEntity.ok(Map.of("mensaje", "Sesión iniciada como " + usuario.getNombre()));
    }
}
