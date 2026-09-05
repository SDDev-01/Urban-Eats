package com.urbaneats.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Gerente;

public interface GerenteRepository extends JpaRepository<Gerente, Integer> {
    Optional<Gerente> findByUsuario_CodigoUsuario(Integer codigoUsuario);
}