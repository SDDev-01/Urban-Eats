package com.urbaneats.service;

import com.urbaneats.entity.Envio;
import java.util.List;
import java.util.Optional;

public interface IEnvioService {
    List<Envio> listarTodos();
    Optional<Envio> buscarPorId(Integer id);
    Envio guardar(Envio envio);
    Envio actualizar(Integer id, Envio envio);
    void eliminar(Integer id);
}