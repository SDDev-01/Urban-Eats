package com.urbaneats.dao;

import com.urbaneats.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoDao extends JpaRepository<Pedido, Long> {
}