package com.dsi.bolsines.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesiones")
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    // usuario 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_nombre", nullable = false)
    private Usuario usuario;

    public Sesion() {
    }

    public Sesion(LocalDateTime fechaHoraInicio, Usuario usuario) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    /** La sesion actual es la que todavia no tiene fecha de fin. */
    public boolean sosActual() {
        return fechaHoraFin == null;
    }

    public void cerrar(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }
}
