package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Plato;

public interface PlatoRepository extends JpaRepository<Plato, Integer> {
}
