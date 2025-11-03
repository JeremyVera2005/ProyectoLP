package com.sistema.rep.servicios.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.repositorios.CitaMedicaRepository;
import com.sistema.rep.servicios.CitaMedicaService;

@Service
public class CitaMedicaServiceImpl implements CitaMedicaService {

    @Autowired
    private CitaMedicaRepository citaMedicaRepository;

    @Override
    public CitaMedica agregarCitaMedica(CitaMedica citaMedica) {
        // Validaciones básicas
        if (citaMedica.getUsuario() == null || citaMedica.getDoctor() == null) {
            throw new IllegalArgumentException("La cita médica debe tener un usuario y un doctor asignados");
        }
        
        if (citaMedica.getFechaCita() == null) {
            throw new IllegalArgumentException("La fecha de la cita es obligatoria");
        }

        // Verificar que la fecha no sea en el pasado
        if (citaMedica.getFechaCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden crear citas en fechas pasadas");
        }

        // Verificar si ya existe una cita para el doctor en esa fecha (opcional)
        if (existeCitaEnFecha(citaMedica.getDoctor(), citaMedica.getFechaCita())) {
            throw new IllegalArgumentException("Ya existe una cita programada para este doctor en esa fecha");
        }

        return citaMedicaRepository.save(citaMedica);
    }

    @Override
    public CitaMedica actualizarCitaMedica(CitaMedica citaMedica) {
        if (citaMedica.getCitaId() == null) {
            throw new IllegalArgumentException("El ID de la cita es requerido para actualizar");
        }

        CitaMedica citaExistente = obtenerCitaMedica(citaMedica.getCitaId());
        if (citaExistente == null) {
            throw new IllegalArgumentException("La cita médica no existe");
        }

        citaMedica.setFechaActualizacion(LocalDateTime.now());
        return citaMedicaRepository.save(citaMedica);
    }

    @Override
    public Set<CitaMedica> obtenerCitasMedicas() {
        return new HashSet<>(citaMedicaRepository.findAll());
    }

    @Override
    public CitaMedica obtenerCitaMedica(Long citaId) {
        return citaMedicaRepository.findById(citaId).orElse(null);
    }

    @Override
    public void eliminarCitaMedica(Long citaId) {
        CitaMedica cita = obtenerCitaMedica(citaId);
        if (cita != null) {
            citaMedicaRepository.delete(cita);
        } else {
            throw new IllegalArgumentException("La cita médica no existe");
        }
    }

    @Override
    public List<CitaMedica> obtenerCitasPorUsuario(Usuario usuario) {
        return citaMedicaRepository.findByUsuario(usuario);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorDoctor(Doctor doctor) {
        return citaMedicaRepository.findByDoctor(doctor);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorEstado(String estado) {
        return citaMedicaRepository.findByEstado(estado);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorUsuarioYEstado(Usuario usuario, String estado) {
        return citaMedicaRepository.findByUsuarioAndEstado(usuario, estado);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorDoctorYEstado(Doctor doctor, String estado) {
        return citaMedicaRepository.findByDoctorAndEstado(doctor, estado);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return citaMedicaRepository.findByFechaCitaBetween(fechaInicio, fechaFin);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorDoctorYFechas(Doctor doctor, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return citaMedicaRepository.findByDoctorAndFechaCitaBetween(doctor, fechaInicio, fechaFin);
    }

    @Override
    public List<CitaMedica> obtenerCitasPorUsuarioYFechas(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return citaMedicaRepository.findByUsuarioAndFechaCitaBetween(usuario, fechaInicio, fechaFin);
    }

    @Override
    public List<CitaMedica> obtenerCitasPendientesProximas() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(1); // Próximas 24 horas
        return citaMedicaRepository.findCitasPendientesProximas(ahora, limite);
    }

    @Override
    public CitaMedica marcarCitaComoCompletada(Long citaId) {
        CitaMedica cita = obtenerCitaMedica(citaId);
        if (cita == null) {
            throw new IllegalArgumentException("La cita médica no existe");
        }

        cita.setEstado("COMPLETADA");
        cita.setFechaActualizacion(LocalDateTime.now());
        return citaMedicaRepository.save(cita);
    }

    @Override
    public CitaMedica cancelarCita(Long citaId, String motivoCancelacion) {
        CitaMedica cita = obtenerCitaMedica(citaId);
        if (cita == null) {
            throw new IllegalArgumentException("La cita médica no existe");
        }

        cita.setEstado("CANCELADA");
        if (motivoCancelacion != null && !motivoCancelacion.trim().isEmpty()) {
            String observacionesActuales = cita.getObservaciones();
            String nuevasObservaciones = observacionesActuales != null ? 
                observacionesActuales + " | CANCELACIÓN: " + motivoCancelacion :
                "CANCELACIÓN: " + motivoCancelacion;
            cita.setObservaciones(nuevasObservaciones);
        }
        cita.setFechaActualizacion(LocalDateTime.now());
        return citaMedicaRepository.save(cita);
    }

    @Override
    public CitaMedica reagendarCita(Long citaId, LocalDateTime nuevaFecha) {
        CitaMedica cita = obtenerCitaMedica(citaId);
        if (cita == null) {
            throw new IllegalArgumentException("La cita médica no existe");
        }

        // Validar que la nueva fecha no sea en el pasado
        if (nuevaFecha.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede reagendar a una fecha pasada");
        }

        // Verificar disponibilidad del doctor en la nueva fecha
        if (existeCitaEnFecha(cita.getDoctor(), nuevaFecha)) {
            throw new IllegalArgumentException("El doctor ya tiene una cita programada en esa fecha");
        }

        cita.setFechaCita(nuevaFecha);
        cita.setEstado("PENDIENTE");
        cita.setFechaActualizacion(LocalDateTime.now());
        return citaMedicaRepository.save(cita);
    }

    @Override
    public Long contarCitasPorEstado(String estado) {
        return citaMedicaRepository.countByEstado(estado);
    }

    @Override
    public Long contarCitasPorDoctor(Doctor doctor) {
        return citaMedicaRepository.countByDoctor(doctor);
    }

    @Override
    public Long contarCitasPorUsuario(Usuario usuario) {
        return citaMedicaRepository.countByUsuario(usuario);
    }

    @Override
    public List<Object[]> obtenerEstadisticasPorDoctor() {
        return citaMedicaRepository.getEstadisticasPorDoctor();
    }

    @Override
    public List<CitaMedica> obtenerCitasUsuariosNoAdministradores() {
        // Esta implementación podría mejorarse con un query específico
        return citaMedicaRepository.findAll().stream()
                .filter(cita -> puedeUsuarioCrearCita(cita.getUsuario()))
                .collect(Collectors.toList());
    }

    @Override
    public CitaMedica crearCitaParaUsuarioNoAdmin(Usuario usuario, Doctor doctor, LocalDateTime fechaCita, String observaciones) {
        // Verificar que el usuario no sea administrador
        if (!puedeUsuarioCrearCita(usuario)) {
            throw new IllegalArgumentException("Solo usuarios no administradores pueden crear citas a través de este método");
        }

        CitaMedica nuevaCita = new CitaMedica(fechaCita, observaciones, usuario, doctor);
        return agregarCitaMedica(nuevaCita);
    }

    @Override
    public boolean puedeUsuarioCrearCita(Usuario usuario) {
        // Verificar que el usuario no tenga rol de administrador
        return usuario.getUsuarioRoles().stream()
                .noneMatch(usuarioRol -> "ADMIN".equals(usuarioRol.getRol().getRolNombre()));
    }

    @Override
    public boolean existeCitaEnFecha(Doctor doctor, LocalDateTime fecha) {
        // Buscar citas en un rango de 1 hora alrededor de la fecha solicitada
        LocalDateTime inicioRango = fecha.minusMinutes(30);
        LocalDateTime finRango = fecha.plusMinutes(30);
        
        List<CitaMedica> citasExistentes = citaMedicaRepository
                .findByDoctorAndFechaCitaBetween(doctor, inicioRango, finRango);
        
        return !citasExistentes.isEmpty();
    }

    @Override
    public boolean puedeModificarCita(Long citaId, Usuario usuario) {
        CitaMedica cita = obtenerCitaMedica(citaId);
        if (cita == null) {
            return false;
        }

        // El usuario puede modificar sus propias citas o si es administrador
        boolean esPropiaCita = cita.getUsuario().getId().equals(usuario.getId());
        boolean esAdmin = usuario.getUsuarioRoles().stream()
                .anyMatch(usuarioRol -> "ADMIN".equals(usuarioRol.getRol().getRolNombre()));

        return esPropiaCita || esAdmin;
    }

    @Override
    public List<CitaMedica> obtenerCitasCompletadasPorUsuario(Usuario usuario) {
        return citaMedicaRepository.findCitasCompletadasPorUsuario(usuario);
    }

    @Override
    public List<CitaMedica> obtenerHistorialCitasUsuario(Usuario usuario) {
        return citaMedicaRepository.findByUsuario(usuario);
    }
}
