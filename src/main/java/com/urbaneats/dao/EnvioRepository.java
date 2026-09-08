package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Envio;


public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    List<Envio> findByRepartidor_CodigoRepartidorAndPedido_EstadoOrderByFechaEnvioDesc(
            Integer codigoRepartidor, String estado);

    List<Envio> findByPedido_EstadoOrderByFechaEnvioDesc(String estado);
}