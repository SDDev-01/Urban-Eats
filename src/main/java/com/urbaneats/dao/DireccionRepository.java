package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    
}
