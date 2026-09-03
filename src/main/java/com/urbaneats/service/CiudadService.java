package com.urbaneats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbaneats.dao.CiudadRepository;
import com.urbaneats.entity.Ciudad;

/**
 * Implementacion de la capa de servicio para Ciudad.
 * Se apoya en CiudadRepository, que sigue vacio: aqui vive la logica.
 */
@Service
public class CiudadService implements ICiudadService {

    private final CiudadRepository ciudadRepository;

    /** Inyeccion por constructor: Spring entrega el repositorio al crear el servicio. */
    public CiudadService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    @Override
    public List<Ciudad> listarCiudades() {
        return ciudadRepository.findAll();
    }

    @Override
    public Ciudad buscarCiudad(Integer codigoCiudad) {
        return ciudadRepository.findById(codigoCiudad).orElse(null);
    }

    @Override
    public Ciudad guardarCiudad(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }

    @Override
    public void eliminarCiudad(Integer codigoCiudad) {
        ciudadRepository.deleteById(codigoCiudad);
    }

    /**
     * El repositorio queda vacio por acuerdo del equipo, asi que el filtro se hace aqui:
     * se traen todas las ciudades y se dejan solo las del departamento pedido.
     * La transaccion de solo lectura mantiene abierta la sesion de Hibernate, porque
     * la relacion con Departamento es LAZY y se lee dentro del recorrido.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ciudad> listarCiudadesPorDepartamento(Integer codigoDepartamento) {
        if (codigoDepartamento == null) {
            return List.of();
        }
        return ciudadRepository.findAll()
                .stream()
                .filter(ciudad -> ciudad.getDepartamento() != null
                        && codigoDepartamento.equals(ciudad.getDepartamento().getCodigoDepartamento()))
                .toList();
    }
}
