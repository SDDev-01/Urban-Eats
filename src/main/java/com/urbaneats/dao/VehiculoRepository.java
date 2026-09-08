package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.Vehiculo;

/**
 * La llave primaria de Vehiculo es la Placa, que en el Schema.sql es VARCHAR(20).
 * Por eso el identificador del repositorio es String y no Integer.
 */
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
}
