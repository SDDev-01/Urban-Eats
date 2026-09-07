package com.urbaneats.service;

import com.urbaneats.entity.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface IVehiculoService {
    List<Vehiculo> listarTodos();
    Optional<Vehiculo> buscarPorId(String placa);
    Vehiculo guardar(Vehiculo vehiculo);
    Vehiculo actualizar(String placa, Vehiculo vehiculo);
    void eliminar(String placa);
}