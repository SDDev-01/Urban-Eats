package com.urbaneats.service;

import java.util.List;

import com.urbaneats.entity.Menu;

/**
 * Contrato de la capa de servicio para Menu.
 * Aqui solo se declara QUE se puede hacer; el COMO vive en MenuService.
 */
public interface IMenuService {

    /** Devuelve todos los menus registrados. */
    List<Menu> listarMenus();

    /** Busca un menu por su codigo. Devuelve null si no existe. */
    Menu buscarMenu(Integer codigoMenu);

    /** Guarda un menu nuevo o actualiza uno existente. */
    Menu guardarMenu(Menu menu);

    /** Elimina el menu con ese codigo. */
    void eliminarMenu(Integer codigoMenu);

    /** Devuelve los menus que pertenecen a un restaurante. */
    List<Menu> listarMenusPorRestaurante(Integer codigoRestaurante);
}
