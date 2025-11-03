package com.sistema.rep.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "respuestas_cuestionario")
public class RespuestaCuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long respuestaId;

    @Column(nullable = false, length = 5000)
    private String respuestaTexto;

    @Column(nullable = false)
    private LocalDateTime fechaRespuesta;

    @Column(nullable = false)
    private Boolean esCorrecta;

    @Column(length = 1000)
    private String observacionesAdicionales;

    // Relación con la pregunta respondida
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    // Relación con la cita médica
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cita_id", nullable = false)
    private CitaMedica citaMedica;

    // Relación con el usuario que respondió
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Constructores
    public RespuestaCuestionario() {
        this.fechaRespuesta = LocalDateTime.now();
        this.esCorrecta = false;
    }

    public RespuestaCuestionario(String respuestaTexto, Pregunta pregunta, CitaMedica citaMedica, Usuario usuario) {
        this();
        this.respuestaTexto = respuestaTexto;
        this.pregunta = pregunta;
        this.citaMedica = citaMedica;
        this.usuario = usuario;
        // Evaluar si la respuesta es correcta comparando con la respuesta de la pregunta
        this.esCorrecta = pregunta.getRespuesta() != null && 
                         pregunta.getRespuesta().equalsIgnoreCase(respuestaTexto.trim());
    }

    // Getters y Setters
    public Long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(Long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public String getRespuestaTexto() {
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto) {
        this.respuestaTexto = respuestaTexto;
        // Re-evaluar si la respuesta es correcta cuando se cambia el texto
        if (this.pregunta != null && this.pregunta.getRespuesta() != null) {
            this.esCorrecta = this.pregunta.getRespuesta().equalsIgnoreCase(respuestaTexto.trim());
        }
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Boolean getEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(Boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public String getObservacionesAdicionales() {
        return observacionesAdicionales;
    }

    public void setObservacionesAdicionales(String observacionesAdicionales) {
        this.observacionesAdicionales = observacionesAdicionales;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
        // Re-evaluar si la respuesta es correcta cuando se cambia la pregunta
        if (pregunta != null && pregunta.getRespuesta() != null && this.respuestaTexto != null) {
            this.esCorrecta = pregunta.getRespuesta().equalsIgnoreCase(this.respuestaTexto.trim());
        }
    }

    public CitaMedica getCitaMedica() {
        return citaMedica;
    }

    public void setCitaMedica(CitaMedica citaMedica) {
        this.citaMedica = citaMedica;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Métodos de utilidad
    public void evaluarRespuesta() {
        if (this.pregunta != null && this.pregunta.getRespuesta() != null && this.respuestaTexto != null) {
            this.esCorrecta = this.pregunta.getRespuesta().equalsIgnoreCase(this.respuestaTexto.trim());
        }
    }

    @Override
    public String toString() {
        return "RespuestaCuestionario{" +
                "respuestaId=" + respuestaId +
                ", respuestaTexto='" + respuestaTexto + '\'' +
                ", fechaRespuesta=" + fechaRespuesta +
                ", esCorrecta=" + esCorrecta +
                ", usuario=" + (usuario != null ? usuario.getUsername() : "null") +
                ", pregunta=" + (pregunta != null ? pregunta.getPreguntaId() : "null") +
                '}';
    }
}
