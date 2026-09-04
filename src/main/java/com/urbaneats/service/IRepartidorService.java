package com.urbaneats.service;

import com.urbaneats.entity.Repartidor;
import java.util.List;
import java.util.Optional;

public interface IRepartidorService {
    List<Repartidor> listarTodos();
    Optional<Repartidor> buscarPorId(Integer id);
    Repartidor guardar(Repartidor repartidor);
    Repartidor actualizar(Integer id, Repartidor repartidor);
    void eliminar(Integer id);
}