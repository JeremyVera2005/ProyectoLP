package com.sistema.rep.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Usuario;

public interface CitaMedicaService {

    // Operaciones CRUD básicas
    CitaMedica agregarCitaMedica(CitaMedica citaMedica);
    CitaMedica actualizarCitaMedica(CitaMedica citaMedica);
    Set<CitaMedica> obtenerCitasMedicas();
    CitaMedica obtenerCitaMedica(Long citaId);
    void eliminarCitaMedica(Long citaId);

    // Operaciones específicas del negocio
    List<CitaMedica> obtenerCitasPorUsuario(Usuario usuario);
    List<CitaMedica> obtenerCitasPorDoctor(Doctor doctor);
    List<CitaMedica> obtenerCitasPorEstado(String estado);
    List<CitaMedica> obtenerCitasPorUsuarioYEstado(Usuario usuario, String estado);
    List<CitaMedica> obtenerCitasPorDoctorYEstado(Doctor doctor, String estado);
    
    // Operaciones con fechas
    List<CitaMedica> obtenerCitasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<CitaMedica> obtenerCitasPorDoctorYFechas(Doctor doctor, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<CitaMedica> obtenerCitasPorUsuarioYFechas(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<CitaMedica> obtenerCitasPendientesProximas();

    // Operaciones de gestión de estado
    CitaMedica marcarCitaComoCompletada(Long citaId);
    CitaMedica cancelarCita(Long citaId, String motivoCancelacion);
    CitaMedica reagendarCita(Long citaId, LocalDateTime nuevaFecha);

    // Operaciones de estadísticas
    Long contarCitasPorEstado(String estado);
    Long contarCitasPorDoctor(Doctor doctor);
    Long contarCitasPorUsuario(Usuario usuario);
    List<Object[]> obtenerEstadisticasPorDoctor();

    // Operaciones específicas para usuarios no administradores
    List<CitaMedica> obtenerCitasUsuariosNoAdministradores();
    CitaMedica crearCitaParaUsuarioNoAdmin(Usuario usuario, Doctor doctor, LocalDateTime fechaCita, String observaciones);

    // Validaciones
    boolean puedeUsuarioCrearCita(Usuario usuario);
    boolean existeCitaEnFecha(Doctor doctor, LocalDateTime fecha);
    boolean puedeModificarCita(Long citaId, Usuario usuario);

    // Operaciones de consulta avanzada
    List<CitaMedica> obtenerCitasCompletadasPorUsuario(Usuario usuario);
    List<CitaMedica> obtenerHistorialCitasUsuario(Usuario usuario);
}
