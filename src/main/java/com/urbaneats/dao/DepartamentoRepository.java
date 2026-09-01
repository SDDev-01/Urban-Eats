package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
}
