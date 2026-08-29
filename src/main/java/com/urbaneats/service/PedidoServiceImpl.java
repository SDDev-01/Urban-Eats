package com.urbaneats.service;

import com.urbaneats.dao.PedidoDao;
import com.urbaneats.domain.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoDao pedidoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos() {
        return pedidoDao.findAll();
    }

    @Override
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoDao.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarPedido(Long id) {
        pedidoDao.deleteById(id);
    }
}