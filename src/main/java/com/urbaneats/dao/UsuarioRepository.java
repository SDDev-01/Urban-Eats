package com.urbaneats.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario> findByCorreo(String correo);
} 
