package com.urbaneats.service;

import java.util.List;

import com.urbaneats.entity.Departamento;

/**
 * Contrato de la capa de servicio para Departamento.
 * Aqui solo se declara QUE se puede hacer; el COMO vive en DepartamentoService.
 */
public interface IDepartamentoService {

    /** Devuelve todos los departamentos registrados. */
    List<Departamento> listarDepartamentos();

    /** Busca un departamento por su codigo DANE. Devuelve null si no existe. */
    Departamento buscarDepartamento(Integer codigoDepartamento);

    /** Guarda un departamento nuevo o actualiza uno existente. */
    Departamento guardarDepartamento(Departamento departamento);

    /** Elimina el departamento con ese codigo. */
    void eliminarDepartamento(Integer codigoDepartamento);
}
