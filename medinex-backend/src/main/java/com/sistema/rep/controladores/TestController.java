package com.sistema.rep.controladores;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@CrossOrigin("*")
public class TestController {

    @GetMapping("/microservicio-status")
    public ResponseEntity<Map<String, Object>> verificarMicroservicio() {
        Map<String, Object> status = Map.of(
            "microservicio", "CitaMedica",
            "estado", "OPERATIVO",
            "version", "1.0.0",
            "descripcion", "Microservicio para gestión de citas médicas y respuestas de cuestionarios implementado exitosamente",
            "funcionalidades", Map.of(
                "citas_medicas", "Gestión completa de citas para usuarios no administradores",
                "respuestas_cuestionario", "Sistema de respuestas con evaluación automática",
                "estadisticas", "Dashboard y métricas personalizadas",
                "seguridad", "Control de acceso basado en roles"
            ),
            "endpoints_principales", Map.of(
                "dashboard", "/microservicio-cita-medica/dashboard",
                "crear_cita", "/citas-medicas/",
                "responder_cuestionario", "/respuestas-cuestionario/responder",
                "estadisticas", "/respuestas-cuestionario/estadisticas/personales"
            )
        );
        
        return ResponseEntity.ok(status);
    }
}
