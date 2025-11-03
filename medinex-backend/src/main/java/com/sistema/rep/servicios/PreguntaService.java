package com.sistema.rep.servicios;

import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Pregunta;

import java.util.Set;

public interface PreguntaService {

    Pregunta agregarPregunta(Pregunta pregunta);

    Pregunta actualizarPregunta(Pregunta pregunta);

    Set<Pregunta> obtenerPreguntas();

    Pregunta obtenerPregunta(Long preguntaId);

    Set<Pregunta> obtenerPreguntasDelDoctor(Doctor doctor);

    void eliminarPregunta(Long preguntaId);

    Pregunta listarPregunta(Long preguntaId);

}
