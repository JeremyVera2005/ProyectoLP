package com.sistema.rep.repositorios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Usuario;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    // Buscar citas por usuario
    List<CitaMedica> findByUsuario(Usuario usuario);

    // Buscar citas por doctor
    List<CitaMedica> findByDoctor(Doctor doctor);

    // Buscar citas por estado
    List<CitaMedica> findByEstado(String estado);

    // Buscar citas por usuario y estado
    List<CitaMedica> findByUsuarioAndEstado(Usuario usuario, String estado);

    // Buscar citas por doctor y estado
    List<CitaMedica> findByDoctorAndEstado(Doctor doctor, String estado);

    // Buscar citas por rango de fechas
    List<CitaMedica> findByFechaCitaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar citas por doctor en un rango de fechas
    @Query("SELECT c FROM CitaMedica c WHERE c.doctor = :doctor AND c.fechaCita BETWEEN :fechaInicio AND :fechaFin")
    List<CitaMedica> findByDoctorAndFechaCitaBetween(@Param("doctor") Doctor doctor, 
                                                     @Param("fechaInicio") LocalDateTime fechaInicio, 
                                                     @Param("fechaFin") LocalDateTime fechaFin);

    // Buscar citas por usuario en un rango de fechas
    @Query("SELECT c FROM CitaMedica c WHERE c.usuario = :usuario AND c.fechaCita BETWEEN :fechaInicio AND :fechaFin")
    List<CitaMedica> findByUsuarioAndFechaCitaBetween(@Param("usuario") Usuario usuario, 
                                                      @Param("fechaInicio") LocalDateTime fechaInicio, 
                                                      @Param("fechaFin") LocalDateTime fechaFin);

    // Contar citas por estado
    Long countByEstado(String estado);

    // Contar citas por doctor
    Long countByDoctor(Doctor doctor);

    // Contar citas por usuario
    Long countByUsuario(Usuario usuario);

    // Buscar citas pendientes próximas (las siguientes 24 horas)
    @Query("SELECT c FROM CitaMedica c WHERE c.estado = 'PENDIENTE' AND c.fechaCita BETWEEN :ahora AND :limite ORDER BY c.fechaCita ASC")
    List<CitaMedica> findCitasPendientesProximas(@Param("ahora") LocalDateTime ahora, @Param("limite") LocalDateTime limite);

    // Buscar citas completadas para un usuario específico
    @Query("SELECT c FROM CitaMedica c WHERE c.usuario = :usuario AND c.estado = 'COMPLETADA' ORDER BY c.fechaCita DESC")
    List<CitaMedica> findCitasCompletadasPorUsuario(@Param("usuario") Usuario usuario);

    // Buscar citas por usuario que no sean administrador
    @Query("SELECT c FROM CitaMedica c JOIN c.usuario u JOIN u.usuarioRoles ur JOIN ur.rol r WHERE c.usuario = :usuario AND r.rolNombre != 'ADMIN'")
    List<CitaMedica> findCitasPorUsuarioNoAdministrador(@Param("usuario") Usuario usuario);

    // Obtener estadísticas de citas por doctor
    @Query("SELECT c.doctor.doctorId, c.doctor.titulo, COUNT(c) as totalCitas, " +
           "SUM(CASE WHEN c.estado = 'COMPLETADA' THEN 1 ELSE 0 END) as citasCompletadas, " +
           "SUM(CASE WHEN c.estado = 'PENDIENTE' THEN 1 ELSE 0 END) as citasPendientes, " +
           "SUM(CASE WHEN c.estado = 'CANCELADA' THEN 1 ELSE 0 END) as citasCanceladas " +
           "FROM CitaMedica c GROUP BY c.doctor.doctorId, c.doctor.titulo")
    List<Object[]> getEstadisticasPorDoctor();
}
