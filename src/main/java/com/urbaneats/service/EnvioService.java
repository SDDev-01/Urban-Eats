package com.urbaneats.service;

import com.urbaneats.entity.Envio;
import com.urbaneats.dao.EnvioDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvioService implements IEnvioService {

    @Autowired
    private EnvioDao envioDao;

    @Override
    public List<Envio> listarTodos() {
        return envioDao.findAll();
    }

    @Override
    public Optional<Envio> buscarPorId(Integer id) {
        return envioDao.findById(id);
    }

    @Override
    public Envio guardar(Envio envio) {
        return envioDao.save(envio);
    }

    @Override
    public Envio actualizar(Integer id, Envio envio) {
        if (envioDao.existsById(id)) {
            envio.setIdEnvio(id);
            return envioDao.save(envio);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        envioDao.deleteById(id);
    }
}