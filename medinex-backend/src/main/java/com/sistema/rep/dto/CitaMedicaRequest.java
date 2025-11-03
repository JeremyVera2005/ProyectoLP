package com.sistema.rep.dto;

import java.time.LocalDateTime;

public class CitaMedicaRequest {
    private Long doctorId;
    private LocalDateTime fechaCita;
    private String observaciones;

    public CitaMedicaRequest() {}

    public CitaMedicaRequest(Long doctorId, LocalDateTime fechaCita, String observaciones) {
        this.doctorId = doctorId;
        this.fechaCita = fechaCita;
        this.observaciones = observaciones;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDateTime fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
