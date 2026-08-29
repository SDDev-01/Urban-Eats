package com.urbaneats.dao;

import com.urbaneats.domain.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioDao extends JpaRepository<Envio, Long> {
}