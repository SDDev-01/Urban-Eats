package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Gerente;

public interface GerenteRepository extends JpaRepository<Gerente, Integer> {
}