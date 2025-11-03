package com.sistema.rep.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long servicioId;

    private String titulo;

    private String descripcion;

    @OneToMany(mappedBy = "servicio",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Doctor> doctores = new LinkedHashSet<>();

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Doctor> getDoctores() {
        return doctores;
    }

    public void setDoctores(Set<Doctor> doctores) {
        this.doctores = doctores;
    }

    public Servicio(){

    }
}
