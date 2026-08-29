package com.urbaneats.service;

import com.urbaneats.dao.RepartidorDao;
import com.urbaneats.domain.Repartidor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RepartidorServiceImpl implements RepartidorService {

    @Autowired
    private RepartidorDao repartidorDao;

    @Override
    @Transactional(readOnly = true)
    public List<Repartidor> listarRepartidores() {
        return repartidorDao.findAll();
    }

    @Override
    @Transactional
    public Repartidor guardarRepartidor(Repartidor repartidor) {
        return repartidorDao.save(repartidor);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Repartidor> obtenerRepartidorPorId(Long id) {
        return repartidorDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarRepartidor(Long id) {
        repartidorDao.deleteById(id);
    }
}