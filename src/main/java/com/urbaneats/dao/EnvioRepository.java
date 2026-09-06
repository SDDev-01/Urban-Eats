package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Envio;


public interface EnvioRepository extends JpaRepository<Envio, Integer> {
}