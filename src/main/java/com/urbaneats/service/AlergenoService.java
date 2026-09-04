package com.urbaneats.service;

import com.urbaneats.entity.Alergeno;
import java.util.List;

public interface IAlergenoService {
    List<Alergeno> obtenerTodos();
    Alergeno obtenerPorId(Integer id);
    Alergeno guardar(Alergeno alergeno);
    Alergeno actualizar(Integer id, Alergeno alergeno);
    void eliminar(Integer id);
}