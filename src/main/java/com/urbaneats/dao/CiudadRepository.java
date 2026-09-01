package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
}
