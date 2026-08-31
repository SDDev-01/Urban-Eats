package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Alergeno;

public interface AlergenoRepository extends JpaRepository<Alergeno, Integer> {
}