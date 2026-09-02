package com.urbaneats.service;

import com.urbaneats.entity.Telefono;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITelefonoService {

    Page<Telefono> listarTodos(Pageable pageable);
    List<Telefono> listarPorUsuario(Integer codigoUsuario);
    Optional<Telefono> buscarPorId(Integer id);
    Telefono guardar(Telefono telefono);
    Telefono actualizar(Integer id, Telefono telefono);
    void eliminar(Integer id);
}