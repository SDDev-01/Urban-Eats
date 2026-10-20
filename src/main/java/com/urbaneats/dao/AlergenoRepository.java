package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Alergeno;

public interface AlergenoRepository extends JpaRepository<Alergeno, Integer> {

    /** Los alergenos de un plato (se muestran en el catalogo). */
    List<Alergeno> findByPlato_CodigoPlato(Integer codigoPlato);
}
