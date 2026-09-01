package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
}
