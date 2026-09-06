package com.urbaneats.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Repartidor;


public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {
    Optional<Repartidor> findByUsuario_CodigoUsuario(Integer codigoUsuario);
}