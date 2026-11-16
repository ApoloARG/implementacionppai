package com.dsi.bolsines.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bolsines")
public class Bolsin {

    @Id
    private Integer numeroBolsin;

    private LocalDate fecha;

    @Column(length = 30)
    private String numeroPrecinto;

    private Double peso;

    // origen 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cm_origen_codigo", nullable = false)
    private ComisionMedica origen;

    // destino 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cm_destino_codigo", nullable = false)
    private ComisionMedica destino;

    // cambioEstado 1..* (unidireccional: el CambioEstadoBolsin no conoce su Bolsin)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bolsin_numero")
    private List<CambioEstadoBolsin> cambioEstado = new ArrayList<>();

    public Bolsin() {
    }

    public Bolsin(Integer numeroBolsin, LocalDate fecha, String numeroPrecinto, Double peso,
                  ComisionMedica origen, ComisionMedica destino) {
        this.numeroBolsin = numeroBolsin;
        this.fecha = fecha;
        this.numeroPrecinto = numeroPrecinto;
        this.peso = peso;
        this.origen = origen;
        this.destino = destino;
    }

    public boolean esTuCMOrigen(ComisionMedica cm) {
        return origen != null && origen.esIgualA(cm);
    }

    /** Busca su cambio de estado actual y le pregunta si es Enviado. */
    public boolean sosEnviado() {
        for (CambioEstadoBolsin cambio : cambioEstado) {
            if (cambio.sosActual()) {
                return cambio.sosEnviado();
            }
        }
        return false;
    }

    public Integer getNumeroBolsin() {
        return numeroBolsin;
    }

    public String getNumeroPrecinto() {
        return numeroPrecinto;
    }

    public String getCodigoCMOrigen() {
        return origen.getCodigo();
    }

    public String getCodigoCMDestino() {
        return destino.getCodigo();
    }

    public ComisionMedica getCMDestino() {
        return destino;
    }

    public ComisionMedica getCMOrigen() {
        return origen;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Double getPeso() {
        return peso;
    }

    public List<CambioEstadoBolsin> getCambioEstado() {
        return cambioEstado;
    }

    public void agregarCambioEstado(CambioEstadoBolsin cambio) {
        this.cambioEstado.add(cambio);
    }
}
