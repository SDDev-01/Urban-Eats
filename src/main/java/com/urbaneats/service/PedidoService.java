package com.urbaneats.service;

import com.urbaneats.entity.Pedido;
import com.urbaneats.dao.PedidoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService implements IPedidoService {

    @Autowired
    private PedidoDao pedidoDao;

    @Override
    public List<Pedido> listarTodos() {
        return pedidoDao.findAll();
    }

    @Override
    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoDao.findById(id);
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        return pedidoDao.save(pedido);
    }

    @Override
    public Pedido actualizar(Integer id, Pedido pedido) {
        if (pedidoDao.existsById(id)) {
            pedido.setIdPedido(id);
            return pedidoDao.save(pedido);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        pedidoDao.deleteById(id);
    }
}