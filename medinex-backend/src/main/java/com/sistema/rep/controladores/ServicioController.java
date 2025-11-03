package com.sistema.rep.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.rep.modelo.Servicio;
import com.sistema.rep.servicios.ServicioService;

@RestController
@RequestMapping("/servicio")
@CrossOrigin("*")
public class ServicioController {

    private static final Logger logger = LoggerFactory.getLogger(ServicioController.class);

    @Autowired
    private ServicioService servicioService;

    @PostMapping("/")
    public ResponseEntity<Servicio> guardarServicio(@RequestBody Servicio servicio){
        if (servicio == null) {
            return ResponseEntity.badRequest().build();
        }
        Servicio servicioGuardada = servicioService.agregarServicio(servicio);
        return ResponseEntity.ok(servicioGuardada);
    }

    @GetMapping("/{servicioId}")
    public ResponseEntity<?> listarServicioPorId(@PathVariable("servicioId") Long servicioId){
        try {
            if (servicioId == null) {
                return ResponseEntity.badRequest().body("El ID del servicio no puede ser null");
            }
            Servicio servicio = servicioService.obtenerServicio(servicioId);
            return ResponseEntity.ok(servicio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> listarServicios(){
        return ResponseEntity.ok(servicioService.obtenerServicios());
    }

    @PutMapping("/")
    public ResponseEntity<?> actualizarServicio(@RequestBody Servicio servicio){
        try {
            if (servicio == null) {
                return ResponseEntity.badRequest().body("El servicio no puede ser null");
            }
            if (servicio.getServicioId() == null) {
                return ResponseEntity.badRequest().body("El ID del servicio es requerido para actualizar");
            }
            Servicio servicioActualizado = servicioService.actualizarServicio(servicio);
            return ResponseEntity.ok(servicioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @DeleteMapping("/{servicioId}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long servicioId){
        logger.info("Iniciando eliminación del servicio con ID: {}", servicioId);
        
        try {
            if (servicioId == null || servicioId <= 0) {
                logger.warn("ID de servicio inválido: {}", servicioId);
                return ResponseEntity.badRequest().body("El ID del servicio debe ser un número positivo");
            }
            
            logger.info("Llamando al servicio para eliminar ID: {}", servicioId);
            servicioService.eliminarServicio(servicioId);
            
            logger.info("Servicio eliminado exitosamente con ID: {}", servicioId);
            return ResponseEntity.ok().body("Servicio eliminado exitosamente");
            
        } catch (RuntimeException e) {
            logger.error("Error específico al eliminar servicio ID {}: {}", servicioId, e.getMessage());
            return ResponseEntity.ok().body("Servicio procesado - " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar servicio ID {}: {}", servicioId, e.getMessage(), e);
            return ResponseEntity.ok().body("Servicio procesado - Error interno");
        }
    }
}
