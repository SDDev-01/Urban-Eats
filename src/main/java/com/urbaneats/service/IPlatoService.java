package com.urbaneats.service;

import java.util.List;

import com.urbaneats.entity.Plato;

/**
 * Contrato de la capa de servicio para Plato.
 * Aqui solo se declara QUE se puede hacer; el COMO vive en PlatoService.
 */
public interface IPlatoService {

    /** Devuelve todos los platos registrados. */
    List<Plato> listarPlatos();

    /** Busca un plato por su codigo. Devuelve null si no existe. */
    Plato buscarPlato(Integer codigoPlato);

    /** Guarda un plato nuevo o actualiza uno existente. */
    Plato guardarPlato(Plato plato);

    /** Elimina el plato con ese codigo. */
    void eliminarPlato(Integer codigoPlato);

    /** Devuelve los platos que pertenecen a un menu. */
    List<Plato> listarPlatosPorMenu(Integer codigoMenu);
}
