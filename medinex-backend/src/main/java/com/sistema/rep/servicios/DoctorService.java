package com.sistema.rep.servicios;

import com.sistema.rep.modelo.Servicio;
import com.sistema.rep.modelo.Doctor;

import java.util.List;
import java.util.Set;

public interface DoctorService {

    Doctor agregarDoctor(Doctor doctor);

    Doctor actualizarDoctor(Doctor doctor);

    Set<Doctor> obtenerDoctores();

    Doctor obtenerDoctor(Long doctorId);

    void eliminarDoctor(Long doctorId);

    List<Doctor> listarDoctoresDeUnaServicio(Servicio servicio);

    List<Doctor> obtenerDoctoresActivos();

    List<Doctor> obtenerDoctoresActivosDeUnaServicio(Servicio servicio);
}
