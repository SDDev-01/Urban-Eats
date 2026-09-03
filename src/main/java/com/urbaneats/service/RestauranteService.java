package com.urbaneats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbaneats.dao.RestauranteRepository;
import com.urbaneats.entity.Restaurante;

/**
 * Implementacion de la capa de servicio para Restaurante.
 * Se apoya en RestauranteRepository, que sigue vacio: aqui vive la logica.
 */
@Service
public class RestauranteService implements IRestauranteService {

    private final RestauranteRepository restauranteRepository;

    /** Inyeccion por constructor: Spring entrega el repositorio al crear el servicio. */
    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    @Override
    public List<Restaurante> listarRestaurantes() {
        return restauranteRepository.findAll();
    }

    @Override
    public Restaurante buscarRestaurante(Integer codigoRestaurante) {
        return restauranteRepository.findById(codigoRestaurante).orElse(null);
    }

    @Override
    public Restaurante guardarRestaurante(Restaurante restaurante) {
        return restauranteRepository.save(restaurante);
    }

    @Override
    public void eliminarRestaurante(Integer codigoRestaurante) {
        restauranteRepository.deleteById(codigoRestaurante);
    }
}
