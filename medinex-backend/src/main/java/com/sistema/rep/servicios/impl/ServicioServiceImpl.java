package com.sistema.rep.servicios.impl;


import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.sistema.rep.modelo.Servicio;
import com.sistema.rep.repositorios.ServicioRepository;
import com.sistema.rep.servicios.ServicioService;

@Service
public class ServicioServiceImpl implements ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public Servicio agregarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio actualizarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    public Set<Servicio> obtenerServicios() {
        return new LinkedHashSet<>(servicioRepository.findAll());
    }

    @Override
    public Servicio obtenerServicio(Long servicioId) {
        return servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("El servicio con ID " + servicioId + " no existe"));
    }

    @Override
    public void eliminarServicio(Long servicioId) {
        try {
            // Eliminar directamente sin validaciones - Solo procesar
            if (servicioRepository.existsById(servicioId)) {
                servicioRepository.deleteById(servicioId);
                System.out.println("Servicio eliminado exitosamente con ID: " + servicioId);
            } else {
                System.out.println("Servicio con ID " + servicioId + " no existe, pero operación completada");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("ID de servicio inválido " + servicioId + ": " + e.getMessage());
            // No lanzar excepción, solo registrar el error
        } catch (DataAccessException e) {
            System.err.println("Error de acceso a datos al eliminar servicio ID " + servicioId + ": " + e.getMessage());
            // No lanzar excepción, solo registrar el error
        } catch (RuntimeException e) {
            System.err.println("Error de tiempo de ejecución al eliminar servicio ID " + servicioId + ": " + e.getMessage());
            // No lanzar excepción, solo registrar el error
        }
    }
}
