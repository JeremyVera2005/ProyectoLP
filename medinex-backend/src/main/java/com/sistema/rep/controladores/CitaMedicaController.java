package com.sistema.rep.controladores;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.servicios.CitaMedicaService;
import com.sistema.rep.servicios.DoctorService;
import com.sistema.rep.servicios.UsuarioService;

@RestController
@RequestMapping("/citas-medicas")
@CrossOrigin("*")
public class CitaMedicaController {

    @Autowired
    private CitaMedicaService citaMedicaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DoctorService doctorService;

    // Crear nueva cita médica (solo usuarios no administradores)
    @PostMapping("/")
    public ResponseEntity<CitaMedica> crearCitaMedica(@RequestBody Map<String, Object> request, Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            // Verificar que el usuario pueda crear citas
            if (!citaMedicaService.puedeUsuarioCrearCita(usuario)) {
                return ResponseEntity.status(403).build(); // Forbidden
            }

            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            Doctor doctor = doctorService.obtenerDoctor(doctorId);
            if (doctor == null) {
                return ResponseEntity.badRequest().build();
            }

            String fechaCitaStr = request.get("fechaCita").toString();
            LocalDateTime fechaCita = LocalDateTime.parse(fechaCitaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            String observaciones = request.get("observaciones") != null ? 
                request.get("observaciones").toString() : "";

            CitaMedica nuevaCita = citaMedicaService.crearCitaParaUsuarioNoAdmin(usuario, doctor, fechaCita, observaciones);
            return ResponseEntity.ok(nuevaCita);

        } catch (DateTimeParseException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener todas las citas (solo administradores)
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<CitaMedica>> obtenerTodasLasCitas() {
        Set<CitaMedica> citas = citaMedicaService.obtenerCitasMedicas();
        return ResponseEntity.ok(citas);
    }

    // Obtener citas del usuario actual
    @GetMapping("/mis-citas")
    public ResponseEntity<List<CitaMedica>> obtenerMisCitas(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            List<CitaMedica> misCitas = citaMedicaService.obtenerCitasPorUsuario(usuario);
            return ResponseEntity.ok(misCitas);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener cita por ID
    @GetMapping("/{citaId}")
    public ResponseEntity<CitaMedica> obtenerCitaPorId(@PathVariable Long citaId, Principal principal) {
        try {
            CitaMedica cita = citaMedicaService.obtenerCitaMedica(citaId);
            if (cita == null) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Verificar permisos: solo el propietario de la cita o un admin pueden verla
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(cita);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar cita médica
    @PutMapping("/{citaId}")
    public ResponseEntity<CitaMedica> actualizarCita(@PathVariable Long citaId, 
                                                     @RequestBody CitaMedica citaActualizada, 
                                                     Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Verificar permisos
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            citaActualizada.setCitaId(citaId);
            CitaMedica cita = citaMedicaService.actualizarCitaMedica(citaActualizada);
            return ResponseEntity.ok(cita);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Marcar cita como completada
    @PutMapping("/{citaId}/completar")
    public ResponseEntity<CitaMedica> completarCita(@PathVariable Long citaId, Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Solo administradores o el propietario pueden completar citas
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            CitaMedica cita = citaMedicaService.marcarCitaComoCompletada(citaId);
            return ResponseEntity.ok(cita);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cancelar cita
    @PutMapping("/{citaId}/cancelar")
    public ResponseEntity<CitaMedica> cancelarCita(@PathVariable Long citaId, 
                                                   @RequestBody Map<String, String> request, 
                                                   Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Verificar permisos
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            String motivoCancelacion = request.get("motivo");
            CitaMedica cita = citaMedicaService.cancelarCita(citaId, motivoCancelacion);
            return ResponseEntity.ok(cita);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Reagendar cita
    @PutMapping("/{citaId}/reagendar")
    public ResponseEntity<CitaMedica> reagendarCita(@PathVariable Long citaId, 
                                                    @RequestBody Map<String, String> request, 
                                                    Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Verificar permisos
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            String nuevaFechaStr = request.get("nuevaFecha");
            LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            CitaMedica cita = citaMedicaService.reagendarCita(citaId, nuevaFecha);
            return ResponseEntity.ok(cita);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar cita (solo administradores)
    @DeleteMapping("/{citaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarCita(@PathVariable Long citaId) {
        try {
            citaMedicaService.eliminarCitaMedica(citaId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener citas por doctor (solo administradores o el mismo doctor)
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaMedica>> obtenerCitasPorDoctor(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorService.obtenerDoctor(doctorId);
            if (doctor == null) {
                return ResponseEntity.notFound().build();
            }

            List<CitaMedica> citas = citaMedicaService.obtenerCitasPorDoctor(doctor);
            return ResponseEntity.ok(citas);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener citas por estado
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaMedica>> obtenerCitasPorEstado(@PathVariable String estado) {
        List<CitaMedica> citas = citaMedicaService.obtenerCitasPorEstado(estado.toUpperCase());
        return ResponseEntity.ok(citas);
    }

    // Obtener citas pendientes próximas
    @GetMapping("/pendientes-proximas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaMedica>> obtenerCitasPendientesProximas() {
        List<CitaMedica> citas = citaMedicaService.obtenerCitasPendientesProximas();
        return ResponseEntity.ok(citas);
    }

    // Obtener estadísticas por doctor
    @GetMapping("/estadisticas/doctores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorDoctor() {
        List<Object[]> estadisticas = citaMedicaService.obtenerEstadisticasPorDoctor();
        return ResponseEntity.ok(estadisticas);
    }

    // Obtener historial de citas del usuario
    @GetMapping("/historial")
    public ResponseEntity<List<CitaMedica>> obtenerHistorialCitas(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            List<CitaMedica> historial = citaMedicaService.obtenerHistorialCitasUsuario(usuario);
            return ResponseEntity.ok(historial);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener citas completadas del usuario
    @GetMapping("/completadas")
    public ResponseEntity<List<CitaMedica>> obtenerCitasCompletadas(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            List<CitaMedica> citasCompletadas = citaMedicaService.obtenerCitasCompletadasPorUsuario(usuario);
            return ResponseEntity.ok(citasCompletadas);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
