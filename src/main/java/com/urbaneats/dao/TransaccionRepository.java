package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Transaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, String> {
}