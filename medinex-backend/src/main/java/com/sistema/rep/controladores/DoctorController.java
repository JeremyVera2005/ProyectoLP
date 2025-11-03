package com.sistema.rep.controladores;

import com.sistema.rep.modelo.Servicio;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.servicios.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@CrossOrigin("*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/")
    public ResponseEntity<Doctor> guardarDoctor(@RequestBody Doctor doctor){
        return ResponseEntity.ok(doctorService.agregarDoctor(doctor));
    }

    @PutMapping("/")
    public ResponseEntity<Doctor> actualizarDoctor(@RequestBody Doctor doctor){
        return ResponseEntity.ok(doctorService.actualizarDoctor(doctor));
    }

    @GetMapping("/")
    public ResponseEntity<?> listarDoctores(){
        return ResponseEntity.ok(doctorService.obtenerDoctores());
    }

    @GetMapping("/{doctorId}")
    public Doctor listarDoctor(@PathVariable("doctorId") Long doctorId){
        return doctorService.obtenerDoctor(doctorId);
    }

    @DeleteMapping("/{doctorId}")
    public void eliminarDoctor(@PathVariable("doctorId") Long doctorId){
        doctorService.eliminarDoctor(doctorId);
    }

    @GetMapping("/servicio/{servicioId}")
    public List<Doctor> listarDoctoresDeUnaServicio(@PathVariable("servicioId") Long servicioId){
        Servicio servicio = new Servicio();
        servicio.setServicioId(servicioId);
        return doctorService.listarDoctoresDeUnaServicio(servicio);
    }

    @GetMapping("/activo")
    public List<Doctor> listarDoctoresActivos(){
        return doctorService.obtenerDoctoresActivos();
    }

    @GetMapping("/servicio/activo/{servicioId}")
    public List<Doctor> listarDoctoresActivosDeUnaServicio(@PathVariable("servicioId") Long servicioId){
        Servicio servicio = new Servicio();
        servicio.setServicioId(servicioId);
        return doctorService.obtenerDoctoresActivosDeUnaServicio(servicio);
    }
}
