package com.urbaneats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbaneats.dao.DepartamentoRepository;
import com.urbaneats.entity.Departamento;

/**
 * Implementacion de la capa de servicio para Departamento.
 * Se apoya en DepartamentoRepository, que sigue vacio: aqui vive la logica.
 */
@Service
public class DepartamentoService implements IDepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    /** Inyeccion por constructor: Spring entrega el repositorio al crear el servicio. */
    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    public List<Departamento> listarDepartamentos() {
        return departamentoRepository.findAll();
    }

    @Override
    public Departamento buscarDepartamento(Integer codigoDepartamento) {
        return departamentoRepository.findById(codigoDepartamento).orElse(null);
    }

    @Override
    public Departamento guardarDepartamento(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    @Override
    public void eliminarDepartamento(Integer codigoDepartamento) {
        departamentoRepository.deleteById(codigoDepartamento);
    }
}
