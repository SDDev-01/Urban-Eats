package com.urbaneats.service;

import com.urbaneats.dao.TransaccionRepository;
import com.urbaneats.entity.Transaccion;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransaccionService implements ITransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    @Override
    public List<Transaccion> obtenerTodos() {
        return (List<Transaccion>) transaccionRepository.findAll();
    }

    @Override
    public Transaccion obtenerPorId(String id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con el ID: " + id));
    }

    @Override
    public Transaccion guardar(Transaccion transaccion) {
        return transaccionRepository.save(transaccion);
    }

    @Override
    public Transaccion actualizar(String id, Transaccion transaccion) {
        Transaccion existente = obtenerPorId(id);
        return transaccionRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        transaccionRepository.deleteById(id);
    }
}