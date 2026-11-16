package com.dsi.bolsines.dto;

public record UsuarioDemoResponse(
        String nombreUsuario,
        String empleado,
        String rol,
        String nombreCM,
        Boolean sesionActiva) {
}
