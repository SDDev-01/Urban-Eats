package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbaneats.model.Menu;

/**
 * DAO de `menu`.
 */
@Repository
public interface MenuDAO extends JpaRepository<Menu, Integer> {

    List<Menu> findByRestaurante_CodigoRestaurante(Integer codigoRestaurante);

    List<Menu> findByCategoriaIgnoreCase(String categoria);

    List<Menu> findByRestaurante_CodigoRestauranteAndCategoriaIgnoreCase(
            Integer codigoRestaurante, String categoria);

    long countByRestaurante_CodigoRestaurante(Integer codigoRestaurante);
}
