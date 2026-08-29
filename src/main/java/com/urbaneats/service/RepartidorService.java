package com.urbaneats.service;

import com.urbaneats.domain.Repartidor;
import java.util.List;
import java.util.Optional;

public interface RepartidorService {
    List<Repartidor> listarRepartidores();
    Repartidor guardarRepartidor(Repartidor repartidor);
    Optional<Repartidor> obtenerRepartidorPorId(Long id);
    void eliminarRepartidor(Long id);
}