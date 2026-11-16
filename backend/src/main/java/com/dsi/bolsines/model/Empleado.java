package com.dsi.bolsines.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    private Long legajo;

    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String email;

    // usuario 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_nombre")
    private Usuario usuario;

    // rol 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_nombre", nullable = false)
    private Rol rol;

    // asignado 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cm_codigo", nullable = false)
    private ComisionMedica asignado;

    public Empleado() {
    }

    public Empleado(Long legajo, String apellido, String nombre, String email,
                    Usuario usuario, Rol rol, ComisionMedica asignado) {
        this.legajo = legajo;
        this.apellido = apellido;
        this.nombre = nombre;
        this.email = email;
        this.usuario = usuario;
        this.rol = rol;
        this.asignado = asignado;
    }

    public boolean esTuUsuario(Usuario usuarioLogueado) {
        return usuario != null && usuarioLogueado != null
                && usuario.getNombre().equals(usuarioLogueado.getNombre());
    }

    public ComisionMedica obtenerCM() {
        return asignado;
    }

    public boolean sosDeCM(ComisionMedica cm) {
        return asignado != null && asignado.esIgualA(cm);
    }

    /** Delega en su Rol (sosGCM) si es Gerente de Comision Medica. */
    public boolean sosGerente() {
        return rol != null && rol.sosGCM();
    }

    public String getEmail() {
        return email;
    }

    public Long getLegajo() {
        return legajo;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
