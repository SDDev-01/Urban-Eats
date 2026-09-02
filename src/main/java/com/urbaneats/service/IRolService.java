package com.urbaneats.service;

import com.urbaneats.entity.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IRolService {

    Page<Rol> listarTodos(Pageable pageable);
    Optional<Rol> buscarPorId(Integer id);
    Optional<Rol> buscarPorNombre(String nombreRol);
    Rol guardar(Rol rol);
    Rol actualizar(Integer id, Rol rol);
    void eliminar(Integer id);
    boolean existePorNombre(String nombreRol);
}
