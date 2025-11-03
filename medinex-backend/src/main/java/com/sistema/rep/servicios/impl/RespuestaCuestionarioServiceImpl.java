package com.sistema.rep.servicios.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Pregunta;
import com.sistema.rep.modelo.RespuestaCuestionario;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.repositorios.RespuestaCuestionarioRepository;
import com.sistema.rep.servicios.RespuestaCuestionarioService;

@Service
public class RespuestaCuestionarioServiceImpl implements RespuestaCuestionarioService {

    @Autowired
    private RespuestaCuestionarioRepository respuestaCuestionarioRepository;

    @Override
    public RespuestaCuestionario agregarRespuesta(RespuestaCuestionario respuesta) {
        // Validaciones básicas
        if (respuesta.getUsuario() == null || respuesta.getCitaMedica() == null || respuesta.getPregunta() == null) {
            throw new IllegalArgumentException("La respuesta debe tener usuario, cita médica y pregunta asignados");
        }

        if (respuesta.getRespuestaTexto() == null || respuesta.getRespuestaTexto().trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de la respuesta es obligatorio");
        }

        // Verificar que el usuario pueda responder esta cita
        if (!puedeUsuarioResponder(respuesta.getUsuario(), respuesta.getCitaMedica())) {
            throw new IllegalArgumentException("El usuario no tiene permisos para responder esta cita médica");
        }

        // Verificar si ya respondió esta pregunta para esta cita
        if (yaRespondidaPregunta(respuesta.getUsuario(), respuesta.getCitaMedica(), respuesta.getPregunta())) {
            throw new IllegalArgumentException("El usuario ya respondió esta pregunta para esta cita");
        }

        // Evaluar automáticamente la respuesta
        evaluarRespuesta(respuesta);

        return respuestaCuestionarioRepository.save(respuesta);
    }

    @Override
    public RespuestaCuestionario actualizarRespuesta(RespuestaCuestionario respuesta) {
        if (respuesta.getRespuestaId() == null) {
            throw new IllegalArgumentException("El ID de la respuesta es requerido para actualizar");
        }

        RespuestaCuestionario respuestaExistente = obtenerRespuesta(respuesta.getRespuestaId());
        if (respuestaExistente == null) {
            throw new IllegalArgumentException("La respuesta no existe");
        }

        // Re-evaluar la respuesta si cambió el texto
        evaluarRespuesta(respuesta);

        return respuestaCuestionarioRepository.save(respuesta);
    }

    @Override
    public Set<RespuestaCuestionario> obtenerTodasLasRespuestas() {
        return new HashSet<>(respuestaCuestionarioRepository.findAll());
    }

    @Override
    public RespuestaCuestionario obtenerRespuesta(Long respuestaId) {
        return respuestaCuestionarioRepository.findById(respuestaId).orElse(null);
    }

    @Override
    public void eliminarRespuesta(Long respuestaId) {
        RespuestaCuestionario respuesta = obtenerRespuesta(respuestaId);
        if (respuesta != null) {
            respuestaCuestionarioRepository.delete(respuesta);
        } else {
            throw new IllegalArgumentException("La respuesta no existe");
        }
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasPorCita(CitaMedica citaMedica) {
        return respuestaCuestionarioRepository.findByCitaMedicaOrderByFechaRespuesta(citaMedica);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasPorUsuario(Usuario usuario) {
        return respuestaCuestionarioRepository.findByUsuario(usuario);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasPorPregunta(Pregunta pregunta) {
        return respuestaCuestionarioRepository.findByPregunta(pregunta);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasPorCitaYUsuario(CitaMedica citaMedica, Usuario usuario) {
        return respuestaCuestionarioRepository.findByCitaMedicaAndUsuario(citaMedica, usuario);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasCorrectas(Usuario usuario) {
        return respuestaCuestionarioRepository.findByUsuarioAndEsCorrecta(usuario, true);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasIncorrectas(Usuario usuario) {
        return respuestaCuestionarioRepository.findByUsuarioAndEsCorrecta(usuario, false);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return respuestaCuestionarioRepository.findByFechaRespuestaBetween(fechaInicio, fechaFin);
    }

    @Override
    public Long contarRespuestasCorrectasPorUsuario(Usuario usuario) {
        return respuestaCuestionarioRepository.countRespuestasCorrectasPorUsuario(usuario);
    }

    @Override
    public Long contarTotalRespuestasPorUsuario(Usuario usuario) {
        return respuestaCuestionarioRepository.countByUsuario(usuario);
    }

    @Override
    public Double obtenerPorcentajeAciertos(Usuario usuario) {
        Double porcentaje = respuestaCuestionarioRepository.getPorcentajeAciertosPorUsuario(usuario);
        return porcentaje != null ? porcentaje : 0.0;
    }

    @Override
    public List<Object[]> obtenerEstadisticasPorPregunta() {
        return respuestaCuestionarioRepository.getEstadisticasPorPregunta();
    }

    @Override
    public List<Object[]> obtenerEstadisticasGenerales() {
        return respuestaCuestionarioRepository.getEstadisticasGenerales();
    }

    @Override
    public RespuestaCuestionario responderPregunta(Usuario usuario, CitaMedica citaMedica, Pregunta pregunta, String respuestaTexto) {
        RespuestaCuestionario nuevaRespuesta = new RespuestaCuestionario(respuestaTexto, pregunta, citaMedica, usuario);
        return agregarRespuesta(nuevaRespuesta);
    }

    @Override
    public RespuestaCuestionario responderPreguntaConObservaciones(Usuario usuario, CitaMedica citaMedica, Pregunta pregunta, String respuestaTexto, String observaciones) {
        RespuestaCuestionario nuevaRespuesta = new RespuestaCuestionario(respuestaTexto, pregunta, citaMedica, usuario);
        nuevaRespuesta.setObservacionesAdicionales(observaciones);
        return agregarRespuesta(nuevaRespuesta);
    }

    @Override
    public List<RespuestaCuestionario> completarCuestionario(Usuario usuario, CitaMedica citaMedica, List<Pregunta> preguntas, List<String> respuestas) {
        if (preguntas.size() != respuestas.size()) {
            throw new IllegalArgumentException("El número de preguntas debe coincidir con el número de respuestas");
        }

        List<RespuestaCuestionario> respuestasGuardadas = new ArrayList<>();

        for (int i = 0; i < preguntas.size(); i++) {
            Pregunta pregunta = preguntas.get(i);
            String respuestaTexto = respuestas.get(i);

            // Verificar si ya respondió esta pregunta
            if (!yaRespondidaPregunta(usuario, citaMedica, pregunta)) {
                RespuestaCuestionario nuevaRespuesta = responderPregunta(usuario, citaMedica, pregunta, respuestaTexto);
                respuestasGuardadas.add(nuevaRespuesta);
            }
        }

        return respuestasGuardadas;
    }

    @Override
    public boolean esCuestionarioCompleto(CitaMedica citaMedica) {
        // Obtener todas las preguntas del doctor de la cita
        if (citaMedica.getDoctor() == null || citaMedica.getDoctor().getPreguntas() == null) {
            return false;
        }

        int totalPreguntas = citaMedica.getDoctor().getPreguntas().size();
        int respuestasCompletadas = obtenerRespuestasPorCita(citaMedica).size();

        return totalPreguntas > 0 && respuestasCompletadas >= totalPreguntas;
    }

    @Override
    public List<RespuestaCuestionario> obtenerUltimasRespuestas(Usuario usuario, int limite) {
        return respuestaCuestionarioRepository.findUltimasRespuestasPorUsuario(usuario)
                .stream()
                .limit(limite)
                .collect(Collectors.toList());
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasPorDoctor(Long doctorId) {
        return respuestaCuestionarioRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<RespuestaCuestionario> obtenerRespuestasUsuariosNoAdministradores() {
        return respuestaCuestionarioRepository.findRespuestasUsuariosNoAdministradores();
    }

    @Override
    public boolean puedeUsuarioResponder(Usuario usuario, CitaMedica citaMedica) {
        // Verificar que el usuario sea el mismo de la cita
        if (!citaMedica.getUsuario().getId().equals(usuario.getId())) {
            return false;
        }

        // Verificar que la cita esté en estado adecuado para responder
        String estadoCita = citaMedica.getEstado();
        return "PENDIENTE".equals(estadoCita) || "COMPLETADA".equals(estadoCita);
    }

    @Override
    public boolean yaRespondidaPregunta(Usuario usuario, CitaMedica citaMedica, Pregunta pregunta) {
        List<RespuestaCuestionario> respuestasExistentes = 
            respuestaCuestionarioRepository.findByUsuarioAndPregunta(usuario, pregunta);
        
        return respuestasExistentes.stream()
                .anyMatch(respuesta -> respuesta.getCitaMedica().getCitaId().equals(citaMedica.getCitaId()));
    }

    @Override
    public void evaluarRespuesta(RespuestaCuestionario respuesta) {
        if (respuesta.getPregunta() != null && respuesta.getPregunta().getRespuesta() != null 
            && respuesta.getRespuestaTexto() != null) {
            
            String respuestaCorrecta = respuesta.getPregunta().getRespuesta().trim();
            String respuestaUsuario = respuesta.getRespuestaTexto().trim();
            
            respuesta.setEsCorrecta(respuestaCorrecta.equalsIgnoreCase(respuestaUsuario));
        } else {
            respuesta.setEsCorrecta(false);
        }
    }

    @Override
    public RespuestaCuestionario obtenerRespuestaUsuarioPregunta(Usuario usuario, Pregunta pregunta) {
        List<RespuestaCuestionario> respuestas = respuestaCuestionarioRepository.findByUsuarioAndPregunta(usuario, pregunta);
        return respuestas.isEmpty() ? null : respuestas.get(0);
    }

    @Override
    public List<RespuestaCuestionario> obtenerHistorialRespuestasUsuario(Usuario usuario) {
        return respuestaCuestionarioRepository.findUltimasRespuestasPorUsuario(usuario);
    }
}
