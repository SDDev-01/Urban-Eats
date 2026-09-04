package com.urbaneats.service;

import com.urbaneats.entity.Cliente;
import com.urbaneats.dao.ClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Override
    public List<Cliente> listarTodos() {
        return clienteDao.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteDao.findById(id);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteDao.save(cliente);
    }

  @Override
    public Cliente actualizar(Integer id, Cliente cliente) {
        if (clienteDao.existsById(id)) {
            // Asignamos el id del parámetro para asegurar que actualice el registro correcto
            cliente.setIdCliente(id); 
            return clienteDao.save(cliente);
        }
        return null;
    }
    @Override
    public void eliminar(Integer id) {
        clienteDao.deleteById(id);
    }
}