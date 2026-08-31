package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.urbaneats.model.Restaurante;

/**
 * DAO de `restaurante`.
 */
@Repository
public interface RestauranteDAO extends JpaRepository<Restaurante, Integer> {

    List<Restaurante> findByCiudad_CodigoCiudad(Integer codigoCiudad);

    List<Restaurante> findByCodigoGerente(Integer codigoGerente);

    List<Restaurante> findByNombreContainingIgnoreCase(String texto);

    /** Restaurantes de un departamento completo (ciudad -> departamento). */
    List<Restaurante> findByCiudad_Departamento_CodigoDepartamento(Integer codigoDepartamento);

    /** Trae el restaurante junto con su ciudad y departamento en una sola consulta. */
    @Query("""
            SELECT r FROM Restaurante r
            JOIN FETCH r.ciudad c
            JOIN FETCH c.departamento
            WHERE r.codigoRestaurante = :codigo
            """)
    Restaurante buscarConUbicacion(@Param("codigo") Integer codigo);
}
