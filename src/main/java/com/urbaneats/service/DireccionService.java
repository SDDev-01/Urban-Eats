package com.urbaneats.service;

import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Usuario;
import com.urbaneats.dao.DireccionRepository;
import com.urbaneats.dao.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DireccionService implements IDireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Direccion> listarTodos(Pageable pageable) {
        return direccionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Direccion> listarPorUsuario(Integer codigoUsuario) {
        return direccionRepository.findByUsuario_CodigoUsuario(codigoUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Direccion> buscarPorId(Integer id) {
        return direccionRepository.findById(id);
    }

    @Override
    @Transactional
    public Direccion guardar(Direccion direccion) {
        validarUsuario(direccion.getUsuario());
        return direccionRepository.save(direccion);
    }

    @Override
    @Transactional
    public Direccion actualizar(Integer id, Direccion direccion) {
        Direccion existente = direccionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Direccion no encontrada con ID: " + id));

        validarUsuario(direccion.getUsuario());

        existente.setDireccion(direccion.getDireccion());
        existente.setUsuario(direccion.getUsuario());

        return direccionRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!direccionRepository.existsById(id)) {
            throw new IllegalArgumentException("Direccion no encontrada con ID: " + id);
        }
        direccionRepository.deleteById(id);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getCodigoUsuario() == null) {
            throw new IllegalArgumentException("La direccion debe estar asociada a un usuario valido");
        }
        if (!usuarioRepository.existsById(usuario.getCodigoUsuario())) {
            throw new IllegalArgumentException("El usuario no existe");
        }
    }
}