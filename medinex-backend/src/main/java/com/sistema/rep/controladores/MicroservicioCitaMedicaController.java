package com.sistema.rep.controladores;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.rep.dto.EstadisticasUsuarioResponse;
import com.sistema.rep.modelo.CitaMedica;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Pregunta;
import com.sistema.rep.modelo.RespuestaCuestionario;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.servicios.CitaMedicaService;
import com.sistema.rep.servicios.DoctorService;
import com.sistema.rep.servicios.RespuestaCuestionarioService;
import com.sistema.rep.servicios.UsuarioService;

@RestController
@RequestMapping("/microservicio-cita-medica")
@CrossOrigin("*")
public class MicroservicioCitaMedicaController {

    @Autowired
    private CitaMedicaService citaMedicaService;

    @Autowired
    private RespuestaCuestionarioService respuestaCuestionarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DoctorService doctorService;

    // Dashboard principal para usuarios no administradores
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboardUsuario(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            // Verificar que sea usuario no administrador
            if (!citaMedicaService.puedeUsuarioCrearCita(usuario)) {
                return ResponseEntity.status(403).build();
            }

            Map<String, Object> dashboard = new HashMap<>();

            // Información de citas
            List<CitaMedica> todasLasCitas = citaMedicaService.obtenerCitasPorUsuario(usuario);
            List<CitaMedica> citasPendientes = citaMedicaService.obtenerCitasPorUsuarioYEstado(usuario, "PENDIENTE");
            List<CitaMedica> citasCompletadas = citaMedicaService.obtenerCitasPorUsuarioYEstado(usuario, "COMPLETADA");
            List<CitaMedica> citasCanceladas = citaMedicaService.obtenerCitasPorUsuarioYEstado(usuario, "CANCELADA");

            // Información de respuestas
            Long totalRespuestas = respuestaCuestionarioService.contarTotalRespuestasPorUsuario(usuario);
            Long respuestasCorrectas = respuestaCuestionarioService.contarRespuestasCorrectasPorUsuario(usuario);
            Double porcentajeAciertos = respuestaCuestionarioService.obtenerPorcentajeAciertos(usuario);

            // Últimas actividades
            List<RespuestaCuestionario> ultimasRespuestas = 
                respuestaCuestionarioService.obtenerUltimasRespuestas(usuario, 5);

            dashboard.put("citas", Map.of(
                "total", todasLasCitas.size(),
                "pendientes", citasPendientes.size(),
                "completadas", citasCompletadas.size(),
                "canceladas", citasCanceladas.size(),
                "recientes", todasLasCitas.stream().limit(3).collect(Collectors.toList())
            ));

            dashboard.put("respuestas", Map.of(
                "total", totalRespuestas,
                "correctas", respuestasCorrectas,
                "incorrectas", totalRespuestas - respuestasCorrectas,
                "porcentajeAciertos", porcentajeAciertos,
                "recientes", ultimasRespuestas
            ));

            dashboard.put("usuario", Map.of(
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "email", usuario.getEmail()
            ));

            return ResponseEntity.ok(dashboard);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Crear cita con cuestionario inicial
    @PostMapping("/crear-cita-completa")
    public ResponseEntity<Map<String, Object>> crearCitaCompleta(@RequestBody Map<String, Object> request, 
                                                                 Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            // Verificar permisos
            if (!citaMedicaService.puedeUsuarioCrearCita(usuario)) {
                return ResponseEntity.status(403).build();
            }

            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            Doctor doctor = doctorService.obtenerDoctor(doctorId);
            if (doctor == null) {
                return ResponseEntity.badRequest().build();
            }

            String fechaCitaStr = request.get("fechaCita").toString();
            LocalDateTime fechaCita = LocalDateTime.parse(fechaCitaStr);
            String observaciones = request.get("observaciones") != null ? 
                request.get("observaciones").toString() : "";

            // Crear la cita
            CitaMedica nuevaCita = citaMedicaService.crearCitaParaUsuarioNoAdmin(
                usuario, doctor, fechaCita, observaciones);

            // Obtener preguntas del doctor para el formulario inicial
            List<Pregunta> preguntasDoctor = doctor.getPreguntas().stream().collect(Collectors.toList());

            Map<String, Object> respuesta = Map.of(
                "cita", nuevaCita,
                "preguntas", preguntasDoctor,
                "mensaje", "Cita creada exitosamente. Complete el cuestionario inicial."
            );

            return ResponseEntity.ok(respuesta);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener cita con sus respuestas asociadas
    @GetMapping("/cita-completa/{citaId}")
    public ResponseEntity<Map<String, Object>> obtenerCitaCompleta(@PathVariable Long citaId, 
                                                                   Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            CitaMedica cita = citaMedicaService.obtenerCitaMedica(citaId);

            if (cita == null) {
                return ResponseEntity.notFound().build();
            }

            // Verificar permisos
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            // Obtener respuestas de la cita
            List<RespuestaCuestionario> respuestas = 
                respuestaCuestionarioService.obtenerRespuestasPorCita(cita);

            // Verificar si el cuestionario está completo
            boolean cuestionarioCompleto = respuestaCuestionarioService.esCuestionarioCompleto(cita);

            // Obtener preguntas del doctor
            List<Pregunta> preguntasDoctor = cita.getDoctor().getPreguntas().stream().collect(Collectors.toList());

            Map<String, Object> citaCompleta = Map.of(
                "cita", cita,
                "respuestas", respuestas,
                "preguntas", preguntasDoctor,
                "cuestionarioCompleto", cuestionarioCompleto,
                "totalPreguntas", preguntasDoctor.size(),
                "respuestasCompletadas", respuestas.size()
            );

            return ResponseEntity.ok(citaCompleta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener estadísticas completas del usuario
    @GetMapping("/estadisticas-completas")
    public ResponseEntity<EstadisticasUsuarioResponse> obtenerEstadisticasCompletas(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            // Estadísticas de respuestas
            Long totalRespuestas = respuestaCuestionarioService.contarTotalRespuestasPorUsuario(usuario);
            Long respuestasCorrectas = respuestaCuestionarioService.contarRespuestasCorrectasPorUsuario(usuario);
            Double porcentajeAciertos = respuestaCuestionarioService.obtenerPorcentajeAciertos(usuario);

            // Estadísticas de citas
            Long totalCitas = citaMedicaService.contarCitasPorUsuario(usuario);
            Long citasCompletadas = (long) citaMedicaService.obtenerCitasPorUsuarioYEstado(usuario, "COMPLETADA").size();
            Long citasPendientes = (long) citaMedicaService.obtenerCitasPorUsuarioYEstado(usuario, "PENDIENTE").size();
            Long citasCanceladas = (long) citaMedicaService.obtenerCitasPorUsuarioYEstado(usuario, "CANCELADA").size();

            EstadisticasUsuarioResponse estadisticas = new EstadisticasUsuarioResponse(
                totalRespuestas,
                respuestasCorrectas,
                totalRespuestas - respuestasCorrectas,
                porcentajeAciertos,
                totalCitas,
                citasCompletadas,
                citasPendientes,
                citasCanceladas
            );

            return ResponseEntity.ok(estadisticas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint para administradores: obtener resumen del microservicio
    @GetMapping("/admin/resumen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerResumenAdmin() {
        try {
            Map<String, Object> resumen = new HashMap<>();

            // Estadísticas de citas
            Long totalCitas = (long) citaMedicaService.obtenerCitasMedicas().size();
            Long citasPendientes = citaMedicaService.contarCitasPorEstado("PENDIENTE");
            Long citasCompletadas = citaMedicaService.contarCitasPorEstado("COMPLETADA");
            Long citasCanceladas = citaMedicaService.contarCitasPorEstado("CANCELADA");

            // Estadísticas de respuestas
            List<Object[]> estadisticasGenerales = respuestaCuestionarioService.obtenerEstadisticasGenerales();
            List<Object[]> estadisticasPorDoctor = citaMedicaService.obtenerEstadisticasPorDoctor();

            // Citas próximas
            List<CitaMedica> citasProximas = citaMedicaService.obtenerCitasPendientesProximas();

            resumen.put("citas", Map.of(
                "total", totalCitas,
                "pendientes", citasPendientes,
                "completadas", citasCompletadas,
                "canceladas", citasCanceladas,
                "proximasPendientes", citasProximas.size()
            ));

            resumen.put("estadisticasGenerales", estadisticasGenerales);
            resumen.put("estadisticasPorDoctor", estadisticasPorDoctor);
            resumen.put("citasProximas", citasProximas);

            return ResponseEntity.ok(resumen);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener actividad reciente del usuario
    @GetMapping("/actividad-reciente")
    public ResponseEntity<Map<String, Object>> obtenerActividadReciente(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            List<CitaMedica> citasRecientes = citaMedicaService.obtenerCitasPorUsuario(usuario)
                .stream().limit(5).collect(Collectors.toList());

            List<RespuestaCuestionario> respuestasRecientes = 
                respuestaCuestionarioService.obtenerUltimasRespuestas(usuario, 10);

            Map<String, Object> actividad = Map.of(
                "citasRecientes", citasRecientes,
                "respuestasRecientes", respuestasRecientes
            );

            return ResponseEntity.ok(actividad);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Verificar estado del microservicio
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> verificarEstado() {
        Map<String, String> estado = Map.of(
            "status", "UP",
            "microservicio", "CitaMedica",
            "version", "1.0.0",
            "descripcion", "Microservicio para gestión de citas médicas y respuestas de cuestionarios"
        );
        return ResponseEntity.ok(estado);
    }
}
