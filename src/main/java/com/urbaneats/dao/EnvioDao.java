package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Envio;


public interface EnvioDao extends JpaRepository<Envio, Integer> {
}