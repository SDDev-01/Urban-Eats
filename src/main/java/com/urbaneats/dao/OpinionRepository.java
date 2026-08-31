package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Opinion;

public interface OpinionRepository extends JpaRepository<Opinion, Integer> {
}