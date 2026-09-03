package com.urbaneats.service;

import java.util.List;

import com.urbaneats.entity.Ciudad;

/**
 * Contrato de la capa de servicio para Ciudad.
 * Aqui solo se declara QUE se puede hacer; el COMO vive en CiudadService.
 */
public interface ICiudadService {

    /** Devuelve todas las ciudades registradas. */
    List<Ciudad> listarCiudades();

    /** Busca una ciudad por su codigo DANE. Devuelve null si no existe. */
    Ciudad buscarCiudad(Integer codigoCiudad);

    /** Guarda una ciudad nueva o actualiza una existente. */
    Ciudad guardarCiudad(Ciudad ciudad);

    /** Elimina la ciudad con ese codigo. */
    void eliminarCiudad(Integer codigoCiudad);

    /** Devuelve las ciudades que pertenecen a un departamento. */
    List<Ciudad> listarCiudadesPorDepartamento(Integer codigoDepartamento);
}
