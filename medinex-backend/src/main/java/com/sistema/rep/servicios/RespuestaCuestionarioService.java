package com.sistema.rep.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Pregunta;
import com.sistema.rep.modelo.RespuestaCuestionario;
import com.sistema.rep.modelo.Usuario;

public interface RespuestaCuestionarioService {

    // Operaciones CRUD básicas
    RespuestaCuestionario agregarRespuesta(RespuestaCuestionario respuesta);
    RespuestaCuestionario actualizarRespuesta(RespuestaCuestionario respuesta);
    Set<RespuestaCuestionario> obtenerTodasLasRespuestas();
    RespuestaCuestionario obtenerRespuesta(Long respuestaId);
    void eliminarRespuesta(Long respuestaId);

    // Operaciones de consulta
    List<RespuestaCuestionario> obtenerRespuestasPorCita(CitaMedica citaMedica);
    List<RespuestaCuestionario> obtenerRespuestasPorUsuario(Usuario usuario);
    List<RespuestaCuestionario> obtenerRespuestasPorPregunta(Pregunta pregunta);
    List<RespuestaCuestionario> obtenerRespuestasPorCitaYUsuario(CitaMedica citaMedica, Usuario usuario);

    // Operaciones de filtrado
    List<RespuestaCuestionario> obtenerRespuestasCorrectas(Usuario usuario);
    List<RespuestaCuestionario> obtenerRespuestasIncorrectas(Usuario usuario);
    List<RespuestaCuestionario> obtenerRespuestasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Operaciones de estadísticas
    Long contarRespuestasCorrectasPorUsuario(Usuario usuario);
    Long contarTotalRespuestasPorUsuario(Usuario usuario);
    Double obtenerPorcentajeAciertos(Usuario usuario);
    List<Object[]> obtenerEstadisticasPorPregunta();
    List<Object[]> obtenerEstadisticasGenerales();

    // Operaciones específicas del negocio
    RespuestaCuestionario responderPregunta(Usuario usuario, CitaMedica citaMedica, Pregunta pregunta, String respuestaTexto);
    RespuestaCuestionario responderPreguntaConObservaciones(Usuario usuario, CitaMedica citaMedica, Pregunta pregunta, String respuestaTexto, String observaciones);
    
    // Operaciones de gestión de cuestionarios
    List<RespuestaCuestionario> completarCuestionario(Usuario usuario, CitaMedica citaMedica, List<Pregunta> preguntas, List<String> respuestas);
    boolean esCuestionarioCompleto(CitaMedica citaMedica);
    
    // Operaciones de análisis
    List<RespuestaCuestionario> obtenerUltimasRespuestas(Usuario usuario, int limite);
    List<RespuestaCuestionario> obtenerRespuestasPorDoctor(Long doctorId);
    List<RespuestaCuestionario> obtenerRespuestasUsuariosNoAdministradores();

    // Validaciones y utilidades
    boolean puedeUsuarioResponder(Usuario usuario, CitaMedica citaMedica);
    boolean yaRespondidaPregunta(Usuario usuario, CitaMedica citaMedica, Pregunta pregunta);
    void evaluarRespuesta(RespuestaCuestionario respuesta);
    
    // Operaciones de consulta específicas
    RespuestaCuestionario obtenerRespuestaUsuarioPregunta(Usuario usuario, Pregunta pregunta);
    List<RespuestaCuestionario> obtenerHistorialRespuestasUsuario(Usuario usuario);
}
