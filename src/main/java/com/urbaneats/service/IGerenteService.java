package com.urbaneats.service;

import com.urbaneats.entity.Gerente;
import java.util.List;
import java.util.Optional;

public interface IGerenteService {

    List<Gerente> listarTodos();

    void guardar(Gerente gerente);

    Optional<Gerente> buscarPorId(Integer id);

    void eliminar(Integer id);
}