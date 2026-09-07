package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
}