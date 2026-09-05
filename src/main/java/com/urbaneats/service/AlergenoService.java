package com.urbaneats.service;

import com.urbaneats.dao.AlergenoRepository;
import com.urbaneats.entity.Alergeno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlergenoService implements IAlergenoService {

    @Autowired
    private AlergenoRepository alergenoRepository;

    @Override
    public List<Alergeno> obtenerTodos() {
        return alergenoRepository.findAll();
    }

    @Override
    public Alergeno obtenerPorId(Integer id) {
        return alergenoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alérgeno no encontrado con el ID: " + id));
    }

    @Override
    public Alergeno guardar(Alergeno alergeno) {
        return alergenoRepository.save(alergeno);
    }

    @Override
    public Alergeno actualizar(Integer id, Alergeno alergeno) {
        Alergeno existente = obtenerPorId(id);
        existente.setNombre(alergeno.getNombre());
        existente.setDescripcion(alergeno.getDescripcion());
        return alergenoRepository.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        alergenoRepository.deleteById(id);
    }
} 
