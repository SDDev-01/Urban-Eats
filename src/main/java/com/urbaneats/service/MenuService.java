package com.urbaneats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbaneats.dao.MenuRepository;
import com.urbaneats.entity.Menu;

/**
 * Implementacion de la capa de servicio para Menu.
 * Se apoya en MenuRepository, que sigue vacio: aqui vive la logica.
 */
@Service
public class MenuService implements IMenuService {

    private final MenuRepository menuRepository;

    /** Inyeccion por constructor: Spring entrega el repositorio al crear el servicio. */
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public List<Menu> listarMenus() {
        return menuRepository.findAll();
    }

    @Override
    public Menu buscarMenu(Integer codigoMenu) {
        return menuRepository.findById(codigoMenu).orElse(null);
    }

    @Override
    public Menu guardarMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public void eliminarMenu(Integer codigoMenu) {
        menuRepository.deleteById(codigoMenu);
    }

    /**
     * Alimenta el select de menus del formulario de plato rapido (perfilRestaurante).
     * El repositorio queda vacio por acuerdo del equipo, asi que el filtro se hace aqui.
     * La transaccion de solo lectura mantiene abierta la sesion de Hibernate, porque
     * la relacion con Restaurante es LAZY y se lee dentro del recorrido.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Menu> listarMenusPorRestaurante(Integer codigoRestaurante) {
        if (codigoRestaurante == null) {
            return List.of();
        }
        return menuRepository.findAll()
                .stream()
                .filter(menu -> menu.getRestaurante() != null
                        && codigoRestaurante.equals(menu.getRestaurante().getCodigoRestaurante()))
                .toList();
    }
}
