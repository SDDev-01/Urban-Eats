package com.urbaneats.service;

import com.urbaneats.dao.ClienteDao;
import com.urbaneats.domain.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteDao.findAll();
    }

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        return clienteDao.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        clienteDao.deleteById(id);
    }
}