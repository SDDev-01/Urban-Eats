package com.urbaneats.dao;

import com.urbaneats.domain.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoDao extends JpaRepository<DetallePedido, Long> {
}