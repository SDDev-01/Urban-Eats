package com.urbaneats.service;

import com.urbaneats.entity.DetallePedido;
import com.urbaneats.dao.DetallePedidoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService implements IDetallePedidoService {

    @Autowired
    private DetallePedidoDao detallePedidoDao;

    @Override
    public List<DetallePedido> listarTodos() {
        return detallePedidoDao.findAll();
    }

    @Override
    public Optional<DetallePedido> buscarPorId(Integer id) {
        return detallePedidoDao.findById(id);
    }

    @Override
    public DetallePedido guardar(DetallePedido detallePedido) {
        return detallePedidoDao.save(detallePedido);
    }

    @Override
    public void eliminar(Integer id) {
        detallePedidoDao.deleteById(id);
    }
}