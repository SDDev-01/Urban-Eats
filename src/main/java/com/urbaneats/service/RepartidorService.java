package com.urbaneats.service;

import com.urbaneats.entity.Repartidor;
import com.urbaneats.dao.RepartidorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepartidorService implements IRepartidorService {

    @Autowired
    private RepartidorDao repartidorDao;

    @Override
    public List<Repartidor> listarTodos() {
        return repartidorDao.findAll();
    }

    @Override
    public Optional<Repartidor> buscarPorId(Integer id) {
        return repartidorDao.findById(id);
    }

    @Override
    public Repartidor guardar(Repartidor repartidor) {
        return repartidorDao.save(repartidor);
    }

    @Override
    public Repartidor actualizar(Integer id, Repartidor repartidor) {
        if (repartidorDao.existsById(id)) {
            repartidor.setIdRepartidor(id);
            return repartidorDao.save(repartidor);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        repartidorDao.deleteById(id);
    }
}