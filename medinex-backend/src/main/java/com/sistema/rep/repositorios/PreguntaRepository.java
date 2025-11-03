package com.sistema.rep.repositorios;

import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.modelo.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PreguntaRepository extends JpaRepository<Pregunta,Long> {

    Set<Pregunta> findByDoctor(Doctor doctor);

}
