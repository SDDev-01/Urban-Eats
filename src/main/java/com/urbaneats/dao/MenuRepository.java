package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findByRestaurante_CodigoRestaurante(Integer codigoRestaurante);
}
