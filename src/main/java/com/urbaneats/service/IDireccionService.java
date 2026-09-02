package com.urbaneats.service;

import com.urbaneats.entity.Direccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IDireccionService {

    Page<Direccion> listarTodos(Pageable pageable);
    List<Direccion> listarPorUsuario(Integer codigoUsuario);
    Optional<Direccion> buscarPorId(Integer id);
    Direccion guardar(Direccion direccion);
    Direccion actualizar(Integer id, Direccion direccion);
    void eliminar(Integer id);
}