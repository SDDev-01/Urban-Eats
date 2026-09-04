package com.urbaneats.service;

import com.urbaneats.dao.GerenteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.urbaneats.dao.gerenterepository.IGerenteDao;
import com.urbaneats.entity.Gerente;

@Service
public class GerenteService implements IGerenteService {

    private final GerenteRepository gerenteRepository;
    @Autowired
    private IGerenteService gerenteDao;

    GerenteService(GerenteRepository gerenteRepository) {
        this.gerenteRepository = gerenteRepository;
    }

    @Override
    public List<Gerente> listarTodos() {
        return (List<Gerente>) gerenteRepository.findAll();
    }

    @Override
    public void guardar(Gerente gerente) {
        gerenteRepository.save(gerente);
    }

    @Override
    public Optional<Gerente> buscarPorId(Long id) {
        return gerenteRepository.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        gerenteRepository.deleteById(id);
    }
}