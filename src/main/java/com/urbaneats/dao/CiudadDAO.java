package com.urbaneats.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbaneats.model.Ciudad;

/**
 * DAO de `ciudad`.
 */
@Repository
public interface CiudadDAO extends JpaRepository<Ciudad, Integer> {

    List<Ciudad> findByDepartamento_CodigoDepartamento(Integer codigoDepartamento);

    List<Ciudad> findByDepartamento_CodigoDepartamentoOrderByNombreAsc(Integer codigoDepartamento);

    Optional<Ciudad> findByNombreIgnoreCase(String nombre);

    List<Ciudad> findByNombreContainingIgnoreCase(String texto);
}
