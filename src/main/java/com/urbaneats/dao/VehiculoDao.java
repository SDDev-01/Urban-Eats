package com.urbaneats.dao;

import com.urbaneats.domain.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoDao extends JpaRepository<Vehiculo, Long> {
}