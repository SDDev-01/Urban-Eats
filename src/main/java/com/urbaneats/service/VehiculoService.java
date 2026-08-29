package com.urbaneats.service;

import com.urbaneats.domain.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface VehiculoService {
    List<Vehiculo> listarVehiculos();
    Vehiculo guardarVehiculo(Vehiculo vehiculo);
    Optional<Vehiculo> obtenerVehiculoPorId(Long id);
    void eliminarVehiculo(Long id);
}