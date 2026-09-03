package com.urbaneats.service;

import java.util.List;

import com.urbaneats.entity.Restaurante;

/**
 * Contrato de la capa de servicio para Restaurante.
 * Aqui solo se declara QUE se puede hacer; el COMO vive en RestauranteService.
 */
public interface IRestauranteService {

    /** Devuelve todos los restaurantes registrados. */
    List<Restaurante> listarRestaurantes();

    /** Busca un restaurante por su codigo. Devuelve null si no existe. */
    Restaurante buscarRestaurante(Integer codigoRestaurante);

    /** Guarda un restaurante nuevo o actualiza uno existente. */
    Restaurante guardarRestaurante(Restaurante restaurante);

    /** Elimina el restaurante con ese codigo. */
    void eliminarRestaurante(Integer codigoRestaurante);
}
