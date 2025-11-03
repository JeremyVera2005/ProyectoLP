package com.sistema.rep.modelo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "citas_medicas")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long citaId;

    @Column(nullable = false)
    private LocalDateTime fechaCita;

    @Column(nullable = false)
    private String estado; // PENDIENTE, COMPLETADA, CANCELADA

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    // Relación con el usuario que solicita la cita (no administrador)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación con el doctor asignado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Relación con las respuestas del cuestionario
    @OneToMany(mappedBy = "citaMedica", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RespuestaCuestionario> respuestasCuestionario = new HashSet<>();

    // Constructores
    public CitaMedica() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    public CitaMedica(LocalDateTime fechaCita, String observaciones, Usuario usuario, Doctor doctor) {
        this();
        this.fechaCita = fechaCita;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.doctor = doctor;
    }

    // Getters y Setters
    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public LocalDateTime getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDateTime fechaCita) {
        this.fechaCita = fechaCita;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Set<RespuestaCuestionario> getRespuestasCuestionario() {
        return respuestasCuestionario;
    }

    public void setRespuestasCuestionario(Set<RespuestaCuestionario> respuestasCuestionario) {
        this.respuestasCuestionario = respuestasCuestionario;
    }

    // Métodos de utilidad
    public void agregarRespuesta(RespuestaCuestionario respuesta) {
        respuestasCuestionario.add(respuesta);
        respuesta.setCitaMedica(this);
    }

    public void removerRespuesta(RespuestaCuestionario respuesta) {
        respuestasCuestionario.remove(respuesta);
        respuesta.setCitaMedica(null);
    }

    @Override
    public String toString() {
        return "CitaMedica{" +
                "citaId=" + citaId +
                ", fechaCita=" + fechaCita +
                ", estado='" + estado + '\'' +
                ", usuario=" + (usuario != null ? usuario.getUsername() : "null") +
                ", doctor=" + (doctor != null ? doctor.getTitulo() : "null") +
                '}';
    }
}
