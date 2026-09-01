package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Vehiculo;

public interface VehiculoDao extends JpaRepository<Vehiculo, Integer> {
}