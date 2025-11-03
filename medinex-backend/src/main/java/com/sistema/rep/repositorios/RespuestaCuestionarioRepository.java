package com.sistema.rep.repositorios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Pregunta;
import com.sistema.rep.modelo.RespuestaCuestionario;
import com.sistema.rep.modelo.Usuario;

public interface RespuestaCuestionarioRepository extends JpaRepository<RespuestaCuestionario, Long> {

    // Buscar respuestas por cita médica
    List<RespuestaCuestionario> findByCitaMedica(CitaMedica citaMedica);

    // Buscar respuestas por usuario
    List<RespuestaCuestionario> findByUsuario(Usuario usuario);

    // Buscar respuestas por pregunta
    List<RespuestaCuestionario> findByPregunta(Pregunta pregunta);

    // Buscar respuestas correctas por usuario
    List<RespuestaCuestionario> findByUsuarioAndEsCorrecta(Usuario usuario, Boolean esCorrecta);

    // Buscar respuestas por cita médica y usuario
    List<RespuestaCuestionario> findByCitaMedicaAndUsuario(CitaMedica citaMedica, Usuario usuario);

    // Buscar respuestas por rango de fechas
    List<RespuestaCuestionario> findByFechaRespuestaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Contar respuestas correctas por usuario
    @Query("SELECT COUNT(r) FROM RespuestaCuestionario r WHERE r.usuario = :usuario AND r.esCorrecta = true")
    Long countRespuestasCorrectasPorUsuario(@Param("usuario") Usuario usuario);

    // Contar total de respuestas por usuario
    Long countByUsuario(Usuario usuario);

    // Obtener porcentaje de aciertos por usuario
    @Query("SELECT (COUNT(r) * 100.0 / (SELECT COUNT(r2) FROM RespuestaCuestionario r2 WHERE r2.usuario = :usuario)) " +
           "FROM RespuestaCuestionario r WHERE r.usuario = :usuario AND r.esCorrecta = true")
    Double getPorcentajeAciertosPorUsuario(@Param("usuario") Usuario usuario);

    // Buscar respuestas por cita médica ordenadas por fecha
    @Query("SELECT r FROM RespuestaCuestionario r WHERE r.citaMedica = :citaMedica ORDER BY r.fechaRespuesta ASC")
    List<RespuestaCuestionario> findByCitaMedicaOrderByFechaRespuesta(@Param("citaMedica") CitaMedica citaMedica);

    // Obtener estadísticas de respuestas por pregunta
    @Query("SELECT r.pregunta.preguntaId, r.pregunta.contenido, COUNT(r) as totalRespuestas, " +
           "SUM(CASE WHEN r.esCorrecta = true THEN 1 ELSE 0 END) as respuestasCorrectas, " +
           "SUM(CASE WHEN r.esCorrecta = false THEN 1 ELSE 0 END) as respuestasIncorrectas " +
           "FROM RespuestaCuestionario r GROUP BY r.pregunta.preguntaId, r.pregunta.contenido")
    List<Object[]> getEstadisticasPorPregunta();

    // Obtener últimas respuestas de un usuario
    @Query("SELECT r FROM RespuestaCuestionario r WHERE r.usuario = :usuario ORDER BY r.fechaRespuesta DESC")
    List<RespuestaCuestionario> findUltimasRespuestasPorUsuario(@Param("usuario") Usuario usuario);

    // Buscar respuestas por doctor (a través de la cita médica)
    @Query("SELECT r FROM RespuestaCuestionario r WHERE r.citaMedica.doctor.doctorId = :doctorId")
    List<RespuestaCuestionario> findByDoctorId(@Param("doctorId") Long doctorId);

    // Obtener respuestas de usuarios no administradores
    @Query("SELECT r FROM RespuestaCuestionario r JOIN r.usuario u JOIN u.usuarioRoles ur JOIN ur.rol ro " +
           "WHERE ro.rolNombre != 'ADMIN' ORDER BY r.fechaRespuesta DESC")
    List<RespuestaCuestionario> findRespuestasUsuariosNoAdministradores();

    // Buscar respuestas por usuario y pregunta específica
    List<RespuestaCuestionario> findByUsuarioAndPregunta(Usuario usuario, Pregunta pregunta);

    // Obtener estadísticas generales de respuestas
    @Query("SELECT COUNT(r) as totalRespuestas, " +
           "SUM(CASE WHEN r.esCorrecta = true THEN 1 ELSE 0 END) as respuestasCorrectas, " +
           "SUM(CASE WHEN r.esCorrecta = false THEN 1 ELSE 0 END) as respuestasIncorrectas, " +
           "COUNT(DISTINCT r.usuario.id) as usuariosUnicos, " +
           "COUNT(DISTINCT r.citaMedica.citaId) as citasConRespuestas " +
           "FROM RespuestaCuestionario r")
    List<Object[]> getEstadisticasGenerales();
}
