package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Telefono;

public interface TelefonoRepository extends JpaRepository<Telefono, Integer>{
    
}
