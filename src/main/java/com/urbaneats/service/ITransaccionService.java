package com.urbaneats.service;

import com.urbaneats.entity.Transaccion;
import java.util.List;

public interface ITransaccionService {
    List<Transaccion> obtenerTodos();
    Transaccion obtenerPorId(Integer id);
    Transaccion guardar(Transaccion transaccion);
    Transaccion actualizar(Integer id, Transaccion transaccion);
    void eliminar(Integer id);
}