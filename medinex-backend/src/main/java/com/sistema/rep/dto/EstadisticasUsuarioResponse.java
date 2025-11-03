package com.sistema.rep.dto;

public class EstadisticasUsuarioResponse {
    private Long totalRespuestas;
    private Long respuestasCorrectas;
    private Long respuestasIncorrectas;
    private Double porcentajeAciertos;
    private Long totalCitas;
    private Long citasCompletadas;
    private Long citasPendientes;
    private Long citasCanceladas;

    public EstadisticasUsuarioResponse() {}

    public EstadisticasUsuarioResponse(Long totalRespuestas, Long respuestasCorrectas, 
                                     Long respuestasIncorrectas, Double porcentajeAciertos,
                                     Long totalCitas, Long citasCompletadas, 
                                     Long citasPendientes, Long citasCanceladas) {
        this.totalRespuestas = totalRespuestas;
        this.respuestasCorrectas = respuestasCorrectas;
        this.respuestasIncorrectas = respuestasIncorrectas;
        this.porcentajeAciertos = porcentajeAciertos;
        this.totalCitas = totalCitas;
        this.citasCompletadas = citasCompletadas;
        this.citasPendientes = citasPendientes;
        this.citasCanceladas = citasCanceladas;
    }

    // Getters y Setters
    public Long getTotalRespuestas() {
        return totalRespuestas;
    }

    public void setTotalRespuestas(Long totalRespuestas) {
        this.totalRespuestas = totalRespuestas;
    }

    public Long getRespuestasCorrectas() {
        return respuestasCorrectas;
    }

    public void setRespuestasCorrectas(Long respuestasCorrectas) {
        this.respuestasCorrectas = respuestasCorrectas;
    }

    public Long getRespuestasIncorrectas() {
        return respuestasIncorrectas;
    }

    public void setRespuestasIncorrectas(Long respuestasIncorrectas) {
        this.respuestasIncorrectas = respuestasIncorrectas;
    }

    public Double getPorcentajeAciertos() {
        return porcentajeAciertos;
    }

    public void setPorcentajeAciertos(Double porcentajeAciertos) {
        this.porcentajeAciertos = porcentajeAciertos;
    }

    public Long getTotalCitas() {
        return totalCitas;
    }

    public void setTotalCitas(Long totalCitas) {
        this.totalCitas = totalCitas;
    }

    public Long getCitasCompletadas() {
        return citasCompletadas;
    }

    public void setCitasCompletadas(Long citasCompletadas) {
        this.citasCompletadas = citasCompletadas;
    }

    public Long getCitasPendientes() {
        return citasPendientes;
    }

    public void setCitasPendientes(Long citasPendientes) {
        this.citasPendientes = citasPendientes;
    }

    public Long getCitasCanceladas() {
        return citasCanceladas;
    }

    public void setCitasCanceladas(Long citasCanceladas) {
        this.citasCanceladas = citasCanceladas;
    }
}
