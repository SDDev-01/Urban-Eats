package com.urbaneats.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Telefono;

public interface TelefonoRepository extends JpaRepository<Telefono, Integer>{
    List<Telefono> findByUsuario_CodigoUsuario(Integer codigoUsuario);
}
