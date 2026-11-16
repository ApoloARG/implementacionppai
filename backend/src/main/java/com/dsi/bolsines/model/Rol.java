package com.dsi.bolsines.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Rol {

    public static final String NOMBRE_GCM = "Gerente de Comision Medica";

    @Id
    @Column(length = 60)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    public Rol() {
    }

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /** Experto: el Rol sabe si es el de Gerente de Comision Medica. */
    public boolean sosGCM() {
        return NOMBRE_GCM.equalsIgnoreCase(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
