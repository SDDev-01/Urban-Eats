package com.urbaneats.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbaneats.model.Departamento;

/**
 * DAO de `departamento`.
 * JpaRepository ya trae: findAll, findById, save, deleteById, count, existsById...
 */
@Repository
public interface DepartamentoDAO extends JpaRepository<Departamento, Integer> {

    Optional<Departamento> findByNombreIgnoreCase(String nombre);

    List<Departamento> findByNombreContainingIgnoreCase(String texto);

    List<Departamento> findAllByOrderByNombreAsc();
}
