package com.urbaneats.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    List<Direccion> findByUsuario_CodigoUsuario(Integer codigoUsuario);
}
