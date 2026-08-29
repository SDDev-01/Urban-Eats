package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    
} 
