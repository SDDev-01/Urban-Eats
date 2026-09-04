package com.urbaneats.service;

import com.urbaneats.entity.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface IVehiculoService {
    List<Vehiculo> listarTodos();
    Optional<Vehiculo> buscarPorId(Integer id);
    Vehiculo guardar(Vehiculo vehiculo);
    Vehiculo actualizar(Integer id, Vehiculo vehiculo);
    void eliminar(Integer id);
}