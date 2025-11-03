package com.sistema.rep.repositorios;

import com.sistema.rep.modelo.Servicio;
import com.sistema.rep.modelo.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    List<Doctor> findByServicio(Servicio servicio);

    List<Doctor> findByActivo(Boolean estado);

    List<Doctor> findByServicioAndActivo(Servicio servicio,Boolean estado);
}
