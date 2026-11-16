package com.dsi.bolsines.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estados")
public class Estado {

    @Id
    private Long id;

    @Column(nullable = false, length = 30)
    private String ambito;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    public Estado() {
    }

    public Estado(Long id, String ambito, String nombre, String descripcion) {
        this.id = id;
        this.ambito = ambito;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public boolean sosEnviado() {
        return "Enviado".equalsIgnoreCase(nombre);
    }

    public String getAmbito() {
        return ambito;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
