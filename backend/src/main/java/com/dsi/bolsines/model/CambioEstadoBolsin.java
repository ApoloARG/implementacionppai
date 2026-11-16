package com.dsi.bolsines.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cambios_estado_bolsin")
public class CambioEstadoBolsin {

    @Id
    private Long id;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    // estadoBolsin 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    // responsableCE 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsable_legajo")
    private Empleado responsable;

    public CambioEstadoBolsin() {
    }

    public CambioEstadoBolsin(Long id, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin,
                              Estado estado, Empleado responsable) {
        this.id = id;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.responsable = responsable;
    }

    public boolean sosActual() {
        return fechaHoraFin == null;
    }

    /** Delega en su Estado. */
    public boolean sosEnviado() {
        return estado != null && estado.sosEnviado();
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public Estado getEstado() {
        return estado;
    }
}
