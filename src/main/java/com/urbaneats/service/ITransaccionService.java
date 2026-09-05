package com.urbaneats.service;

import com.urbaneats.entity.Transaccion;
import java.util.List;

public interface ITransaccionService {
    List<Transaccion> obtenerTodos();
    Transaccion obtenerPorId(String id);
    Transaccion guardar(Transaccion transaccion);
    Transaccion actualizar(String id, Transaccion transaccion);
    void eliminar(String id);
}