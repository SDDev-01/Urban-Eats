package com.urbaneats.service;

import com.urbaneats.domain.Envio;
import java.util.List;
import java.util.Optional;

public interface EnvioService {
    List<Envio> listarEnvios();
    Envio guardarEnvio(Envio envio);
    Optional<Envio> obtenerEnvioPorId(Long id);
    void eliminarEnvio(Long id);
}