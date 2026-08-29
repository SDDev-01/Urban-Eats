package com.urbaneats.service;

import com.urbaneats.dao.EnvioDao;
import com.urbaneats.domain.Envio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnvioServiceImpl implements EnvioService {

    @Autowired
    private EnvioDao envioDao;

    @Override
    @Transactional(readOnly = true)
    public List<Envio> listarEnvios() {
        return envioDao.findAll();
    }

    @Override
    @Transactional
    public Envio guardarEnvio(Envio envio) {
        return envioDao.save(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Envio> obtenerEnvioPorId(Long id) {
        return envioDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarEnvio(Long id) {
        envioDao.deleteById(id);
    }
}