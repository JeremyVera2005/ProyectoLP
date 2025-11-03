package com.sistema.rep.controladores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Pregunta;
import com.sistema.rep.servicios.DoctorService;
import com.sistema.rep.servicios.PreguntaService;

@RestController
@RequestMapping("/pregunta")
@CrossOrigin("*")
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/")
    public ResponseEntity<Pregunta> guardarPregunta(@RequestBody Pregunta pregunta){
        return ResponseEntity.ok(preguntaService.agregarPregunta(pregunta));
    }

    @PutMapping("/")
    public ResponseEntity<Pregunta> actualizarPregunta(@RequestBody Pregunta pregunta){
        return ResponseEntity.ok(preguntaService.actualizarPregunta(pregunta));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> listarPreguntasDelDoctor(@PathVariable("doctorId") Long doctorId){
        Doctor doctor = doctorService.obtenerDoctor(doctorId);
        Set<Pregunta> preguntas = doctor.getPreguntas();

        List<Pregunta> preguntasList = new ArrayList<>(preguntas);
        if(preguntasList.size() > Integer.parseInt(doctor.getNumeroDePreguntas())){
            preguntasList = preguntasList.subList(0, Integer.parseInt(doctor.getNumeroDePreguntas()));
        }

        Collections.shuffle(preguntasList);
        return ResponseEntity.ok(preguntasList);
    }

    @GetMapping("/{preguntaId}")
    public Pregunta listarPreguntaPorId(@PathVariable("preguntaId") Long preguntaId){
        return preguntaService.obtenerPregunta(preguntaId);
    }

    @DeleteMapping("/{preguntaId}")
    public void eliminarPregunta(@PathVariable("preguntaId") Long preguntaId){
        preguntaService.eliminarPregunta(preguntaId);
    }

    @GetMapping("/doctor/todos/{doctorId}")
    public ResponseEntity<?> listarPreguntaDelDoctorComoAdministrador(@PathVariable("doctorId") Long doctorId){
        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        Set<Pregunta> preguntas = preguntaService.obtenerPreguntasDelDoctor(doctor);
        return ResponseEntity.ok(preguntas);
    }

    @PostMapping("/evaluar-doctor")
    public ResponseEntity<?> evaluarDoctor(@RequestBody List<Pregunta> preguntas){
        double puntosMaximos = 0;
        Integer respuestasCorrectas = 0;
        Integer intentos = 0;

        for(Pregunta p : preguntas){
            Pregunta pregunta = this.preguntaService.listarPregunta(p.getPreguntaId());
            if(pregunta.getRespuesta().equals(p.getRespuestaDada())){
                respuestasCorrectas ++;
                double puntos = Double.parseDouble(preguntas.get(0).getDoctor().getPuntosMaximos())/preguntas.size();
                puntosMaximos += puntos;
            }
            if(p.getRespuestaDada() != null){
                intentos ++;
            }
        }

        Map<String,Object> respuestas = new HashMap<>();
        respuestas.put("puntosMaximos",puntosMaximos);
        respuestas.put("respuestasCorrectas",respuestasCorrectas);
        respuestas.put("intentos",intentos);
        return ResponseEntity.ok(respuestas);
    }
}
