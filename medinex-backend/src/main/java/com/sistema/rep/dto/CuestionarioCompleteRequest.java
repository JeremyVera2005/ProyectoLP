package com.sistema.rep.dto;

import java.util.List;

public class CuestionarioCompleteRequest {
    private Long citaId;
    private List<RespuestaRequest> respuestas;

    public CuestionarioCompleteRequest() {}

    public CuestionarioCompleteRequest(Long citaId, List<RespuestaRequest> respuestas) {
        this.citaId = citaId;
        this.respuestas = respuestas;
    }

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public List<RespuestaRequest> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<RespuestaRequest> respuestas) {
        this.respuestas = respuestas;
    }

    public static class RespuestaRequest {
        private Long preguntaId;
        private String respuestaTexto;
        private String observaciones;

        public RespuestaRequest() {}

        public RespuestaRequest(Long preguntaId, String respuestaTexto, String observaciones) {
            this.preguntaId = preguntaId;
            this.respuestaTexto = respuestaTexto;
            this.observaciones = observaciones;
        }

        public Long getPreguntaId() {
            return preguntaId;
        }

        public void setPreguntaId(Long preguntaId) {
            this.preguntaId = preguntaId;
        }

        public String getRespuestaTexto() {
            return respuestaTexto;
        }

        public void setRespuestaTexto(String respuestaTexto) {
            this.respuestaTexto = respuestaTexto;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
}
