package com.urbaneats.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.urbaneats.model.Plato;

/**
 * DAO de `plato`.
 */
@Repository
public interface PlatoDAO extends JpaRepository<Plato, Integer> {

    List<Plato> findByMenu_CodigoMenu(Integer codigoMenu);

    List<Plato> findByNombreContainingIgnoreCase(String texto);

    List<Plato> findByTipoComidaIgnoreCase(String tipoComida);

    List<Plato> findByPrecioBetween(BigDecimal minimo, BigDecimal maximo);

    /** Catalogo completo de un restaurante: todos los platos de todos sus menus. */
    @Query("""
            SELECT p FROM Plato p
            JOIN p.menu m
            WHERE m.restaurante.codigoRestaurante = :codigoRestaurante
            ORDER BY m.categoria ASC, p.nombre ASC
            """)
    List<Plato> buscarPorRestaurante(@Param("codigoRestaurante") Integer codigoRestaurante);
}
