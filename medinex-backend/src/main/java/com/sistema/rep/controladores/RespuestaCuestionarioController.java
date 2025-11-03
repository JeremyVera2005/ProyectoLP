package com.sistema.rep.controladores;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.sistema.rep.modelo.Pregunta;
import com.sistema.rep.modelo.RespuestaCuestionario;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.servicios.CitaMedicaService;
import com.sistema.rep.servicios.PreguntaService;
import com.sistema.rep.servicios.RespuestaCuestionarioService;
import com.sistema.rep.servicios.UsuarioService;

@RestController
@RequestMapping("/respuestas-cuestionario")
@CrossOrigin("*")
public class RespuestaCuestionarioController {

    @Autowired
    private RespuestaCuestionarioService respuestaCuestionarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CitaMedicaService citaMedicaService;

    @Autowired
    private PreguntaService preguntaService;

    // Responder una pregunta específica
    @PostMapping("/responder")
    public ResponseEntity<RespuestaCuestionario> responderPregunta(@RequestBody Map<String, Object> request, 
                                                                   Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            Long citaId = Long.valueOf(request.get("citaId").toString());
            Long preguntaId = Long.valueOf(request.get("preguntaId").toString());
            String respuestaTexto = request.get("respuestaTexto").toString();
            String observaciones = request.get("observaciones") != null ? 
                request.get("observaciones").toString() : null;

            CitaMedica cita = citaMedicaService.obtenerCitaMedica(citaId);
            Pregunta pregunta = preguntaService.obtenerPregunta(preguntaId);

            if (cita == null || pregunta == null) {
                return ResponseEntity.badRequest().build();
            }

            // Verificar permisos
            if (!respuestaCuestionarioService.puedeUsuarioResponder(usuario, cita)) {
                return ResponseEntity.status(403).build();
            }

            RespuestaCuestionario nuevaRespuesta;
            if (observaciones != null && !observaciones.trim().isEmpty()) {
                nuevaRespuesta = respuestaCuestionarioService.responderPreguntaConObservaciones(
                    usuario, cita, pregunta, respuestaTexto, observaciones);
            } else {
                nuevaRespuesta = respuestaCuestionarioService.responderPregunta(
                    usuario, cita, pregunta, respuestaTexto);
            }

            return ResponseEntity.ok(nuevaRespuesta);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Completar cuestionario completo
    @PostMapping("/completar-cuestionario")
    public ResponseEntity<List<RespuestaCuestionario>> completarCuestionario(@RequestBody Map<String, Object> request, 
                                                                             Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            Long citaId = Long.valueOf(request.get("citaId").toString());
            CitaMedica cita = citaMedicaService.obtenerCitaMedica(citaId);

            if (cita == null) {
                return ResponseEntity.badRequest().build();
            }

            // Verificar permisos
            if (!respuestaCuestionarioService.puedeUsuarioResponder(usuario, cita)) {
                return ResponseEntity.status(403).build();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> respuestasData = (List<Map<String, Object>>) request.get("respuestas");
            
            List<Pregunta> preguntas = respuestasData.stream()
                .map(data -> preguntaService.obtenerPregunta(Long.valueOf(data.get("preguntaId").toString())))
                .collect(Collectors.toList());

            List<String> respuestas = respuestasData.stream()
                .map(data -> data.get("respuestaTexto").toString())
                .collect(Collectors.toList());

            List<RespuestaCuestionario> respuestasGuardadas = 
                respuestaCuestionarioService.completarCuestionario(usuario, cita, preguntas, respuestas);

            return ResponseEntity.ok(respuestasGuardadas);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener todas las respuestas (solo administradores)
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<RespuestaCuestionario>> obtenerTodasLasRespuestas() {
        Set<RespuestaCuestionario> respuestas = respuestaCuestionarioService.obtenerTodasLasRespuestas();
        return ResponseEntity.ok(respuestas);
    }

    // Obtener respuestas por cita
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<RespuestaCuestionario>> obtenerRespuestasPorCita(@PathVariable Long citaId, 
                                                                                Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            CitaMedica cita = citaMedicaService.obtenerCitaMedica(citaId);

            if (cita == null) {
                return ResponseEntity.notFound().build();
            }

            // Verificar permisos: solo el propietario de la cita o un admin pueden ver las respuestas
            if (!citaMedicaService.puedeModificarCita(citaId, usuario)) {
                return ResponseEntity.status(403).build();
            }

            List<RespuestaCuestionario> respuestas = respuestaCuestionarioService.obtenerRespuestasPorCita(cita);
            return ResponseEntity.ok(respuestas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener mis respuestas
    @GetMapping("/mis-respuestas")
    public ResponseEntity<List<RespuestaCuestionario>> obtenerMisRespuestas(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            List<RespuestaCuestionario> misRespuestas = respuestaCuestionarioService.obtenerRespuestasPorUsuario(usuario);
            return ResponseEntity.ok(misRespuestas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener respuesta por ID
    @GetMapping("/{respuestaId}")
    public ResponseEntity<RespuestaCuestionario> obtenerRespuestaPorId(@PathVariable Long respuestaId, 
                                                                       Principal principal) {
        try {
            RespuestaCuestionario respuesta = respuestaCuestionarioService.obtenerRespuesta(respuestaId);
            if (respuesta == null) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Verificar permisos: solo el propietario de la respuesta o un admin pueden verla
            boolean esPropia = respuesta.getUsuario().getId().equals(usuario.getId());
            boolean esAdmin = usuario.getUsuarioRoles().stream()
                .anyMatch(ur -> "ADMIN".equals(ur.getRol().getRolNombre()));

            if (!esPropia && !esAdmin) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar respuesta
    @PutMapping("/{respuestaId}")
    public ResponseEntity<RespuestaCuestionario> actualizarRespuesta(@PathVariable Long respuestaId, 
                                                                     @RequestBody RespuestaCuestionario respuestaActualizada, 
                                                                     Principal principal) {
        try {
            RespuestaCuestionario respuestaExistente = respuestaCuestionarioService.obtenerRespuesta(respuestaId);
            if (respuestaExistente == null) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            
            // Solo el propietario puede actualizar su respuesta
            if (!respuestaExistente.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(403).build();
            }

            respuestaActualizada.setRespuestaId(respuestaId);
            RespuestaCuestionario respuestaGuardada = respuestaCuestionarioService.actualizarRespuesta(respuestaActualizada);
            return ResponseEntity.ok(respuestaGuardada);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar respuesta (solo administradores)
    @DeleteMapping("/{respuestaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarRespuesta(@PathVariable Long respuestaId) {
        try {
            respuestaCuestionarioService.eliminarRespuesta(respuestaId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener mis estadísticas
    @GetMapping("/estadisticas/personales")
    public ResponseEntity<Map<String, Object>> obtenerMisEstadisticas(Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            Long totalRespuestas = respuestaCuestionarioService.contarTotalRespuestasPorUsuario(usuario);
            Long respuestasCorrectas = respuestaCuestionarioService.contarRespuestasCorrectasPorUsuario(usuario);
            Double porcentajeAciertos = respuestaCuestionarioService.obtenerPorcentajeAciertos(usuario);

            Map<String, Object> estadisticas = Map.of(
                "totalRespuestas", totalRespuestas,
                "respuestasCorrectas", respuestasCorrectas,
                "respuestasIncorrectas", totalRespuestas - respuestasCorrectas,
                "porcentajeAciertos", porcentajeAciertos
            );

            return ResponseEntity.ok(estadisticas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener estadísticas por pregunta (solo administradores)
    @GetMapping("/estadisticas/preguntas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorPregunta() {
        List<Object[]> estadisticas = respuestaCuestionarioService.obtenerEstadisticasPorPregunta();
        return ResponseEntity.ok(estadisticas);
    }

    // Obtener estadísticas generales (solo administradores)
    @GetMapping("/estadisticas/generales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasGenerales() {
        List<Object[]> estadisticas = respuestaCuestionarioService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }

    // Obtener respuestas por doctor (solo administradores)
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RespuestaCuestionario>> obtenerRespuestasPorDoctor(@PathVariable Long doctorId) {
        List<RespuestaCuestionario> respuestas = respuestaCuestionarioService.obtenerRespuestasPorDoctor(doctorId);
        return ResponseEntity.ok(respuestas);
    }

    // Obtener respuestas de usuarios no administradores (solo administradores)
    @GetMapping("/usuarios-no-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RespuestaCuestionario>> obtenerRespuestasUsuariosNoAdmin() {
        List<RespuestaCuestionario> respuestas = respuestaCuestionarioService.obtenerRespuestasUsuariosNoAdministradores();
        return ResponseEntity.ok(respuestas);
    }

    // Obtener mis últimas respuestas
    @GetMapping("/ultimas/{limite}")
    public ResponseEntity<List<RespuestaCuestionario>> obtenerUltimasRespuestas(@PathVariable int limite, 
                                                                                Principal principal) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(principal.getName());
            if (usuario == null) {
                return ResponseEntity.badRequest().build();
            }

            List<RespuestaCuestionario> ultimasRespuestas = 
                respuestaCuestionarioService.obtenerUltimasRespuestas(usuario, limite);
            return ResponseEntity.ok(ultimasRespuestas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Verificar si un cuestionario está completo
    @GetMapping("/cita/{citaId}/completo")
    public ResponseEntity<Map<String, Boolean>> verificarCuestionarioCompleto(@PathVariable Long citaId, 
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

            boolean completo = respuestaCuestionarioService.esCuestionarioCompleto(cita);
            Map<String, Boolean> resultado = Map.of("completo", completo);
            return ResponseEntity.ok(resultado);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
