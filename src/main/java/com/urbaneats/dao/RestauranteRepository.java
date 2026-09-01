package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
}
