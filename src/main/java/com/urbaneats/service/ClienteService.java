package com.urbaneats.service;

import com.urbaneats.domain.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listarClientes();
    Cliente guardarCliente(Cliente cliente);
    Optional<Cliente> obtenerClientePorId(Long id);
    void eliminarCliente(Long id);
}