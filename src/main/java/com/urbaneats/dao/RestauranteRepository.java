package com.urbaneats.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    List<Restaurante> findByGerente_CodigoGerente(Integer codigoGerente);
    boolean existsByGerente_CodigoGerenteAndCodigoRestaurante(Integer codigoGerente, Integer codigoRestaurante);
}
