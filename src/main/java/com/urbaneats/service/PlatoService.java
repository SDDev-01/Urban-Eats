package com.urbaneats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbaneats.dao.PlatoRepository;
import com.urbaneats.entity.Plato;

/**
 * Implementacion de la capa de servicio para Plato.
 * Se apoya en PlatoRepository, que sigue vacio: aqui vive la logica.
 */
@Service
public class PlatoService implements IPlatoService {

    private final PlatoRepository platoRepository;

    /** Inyeccion por constructor: Spring entrega el repositorio al crear el servicio. */
    public PlatoService(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    @Override
    public List<Plato> listarPlatos() {
        return platoRepository.findAll();
    }

    @Override
    public Plato buscarPlato(Integer codigoPlato) {
        return platoRepository.findById(codigoPlato).orElse(null);
    }

    @Override
    public Plato guardarPlato(Plato plato) {
        return platoRepository.save(plato);
    }

    @Override
    public void eliminarPlato(Integer codigoPlato) {
        platoRepository.deleteById(codigoPlato);
    }

    /**
     * Agrupa los platos de un menu (perfilRestaurante los muestra menu por menu).
     * El repositorio queda vacio por acuerdo del equipo, asi que el filtro se hace aqui.
     * Ojo: Plato.menu admite NULL en el Schema, por eso se descarta antes de comparar.
     * La transaccion de solo lectura mantiene abierta la sesion de Hibernate, porque
     * la relacion con Menu es LAZY y se lee dentro del recorrido.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Plato> listarPlatosPorMenu(Integer codigoMenu) {
        if (codigoMenu == null) {
            return List.of();
        }
        return platoRepository.findAll()
                .stream()
                .filter(plato -> plato.getMenu() != null
                        && codigoMenu.equals(plato.getMenu().getCodigoMenu()))
                .toList();
    }
}
