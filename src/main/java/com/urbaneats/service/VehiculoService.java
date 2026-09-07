package com.urbaneats.service;

import com.urbaneats.entity.Vehiculo;
import com.urbaneats.dao.VehiculoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService implements IVehiculoService {

    @Autowired
    private VehiculoDao vehiculoDao;

    @Override
    public List<Vehiculo> listarTodos() {
        return vehiculoDao.findAll();
    }

    @Override
    public Optional<Vehiculo> buscarPorId(String placa) {
        return vehiculoDao.findById(placa);
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoDao.save(vehiculo);
    }

    @Override
    public Vehiculo actualizar(String placa, Vehiculo vehiculo) {
        if (vehiculoDao.existsById(placa)) {
            vehiculo.setPlaca(placa);
            return vehiculoDao.save(vehiculo);
        }
        return null;
    }

    @Override
    public void eliminar(String placa) {
        vehiculoDao.deleteById(placa);
    }
}