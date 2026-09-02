package com.urbaneats.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer>{
    Optional<Rol> findByNombreRol(String nombreRol);
    
}
