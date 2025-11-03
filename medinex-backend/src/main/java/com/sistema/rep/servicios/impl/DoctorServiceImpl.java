package com.sistema.rep.servicios.impl;

import com.sistema.rep.modelo.Servicio;
import com.sistema.rep.modelo.Doctor;
import com.sistema.rep.repositorios.DoctorRepository;
import com.sistema.rep.servicios.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Doctor agregarDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor actualizarDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public Set<Doctor> obtenerDoctores() {
        return new LinkedHashSet<>(doctorRepository.findAll());
    }

    @Override
    public Doctor obtenerDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    @Override
    public void eliminarDoctor(Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctorRepository.delete(doctor);
    }

    @Override
    public List<Doctor> listarDoctoresDeUnaServicio(Servicio servicio) {
        return this.doctorRepository.findByServicio(servicio);
    }

    @Override
    public List<Doctor> obtenerDoctoresActivos() {
        return doctorRepository.findByActivo(true);
    }

    @Override
    public List<Doctor> obtenerDoctoresActivosDeUnaServicio(Servicio servicio) {
        return doctorRepository.findByServicioAndActivo(servicio,true);
    }
}
