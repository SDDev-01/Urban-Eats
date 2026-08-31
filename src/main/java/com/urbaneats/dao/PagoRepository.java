package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
}