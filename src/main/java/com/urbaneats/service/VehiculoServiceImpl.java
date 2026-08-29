package com.urbaneats.service;

import com.urbaneats.dao.VehiculoDao;
import com.urbaneats.domain.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private VehiculoDao vehiculoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos() {
        return vehiculoDao.findAll();
    }

    @Override
    @Transactional
    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
        return vehiculoDao.save(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> obtenerVehiculoPorId(Long id) {
        return vehiculoDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarVehiculo(Long id) {
        vehiculoDao.deleteById(id);
    }
}