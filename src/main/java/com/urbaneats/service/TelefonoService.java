package com.urbaneats.service;

import com.urbaneats.entity.Telefono;
import com.urbaneats.entity.Usuario;
import com.urbaneats.dao.TelefonoRepository;
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
public class TelefonoService implements ITelefonoService {

    private final TelefonoRepository telefonoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Telefono> listarTodos(Pageable pageable) {
        return telefonoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Telefono> listarPorUsuario(Integer codigoUsuario) {
        return telefonoRepository.findByUsuario_CodigoUsuario(codigoUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Telefono> buscarPorId(Integer id) {
        return telefonoRepository.findById(id);
    }

    @Override
    @Transactional
    public Telefono guardar(Telefono telefono) {
        validarUsuario(telefono.getUsuario());
        return telefonoRepository.save(telefono);
    }

    @Override
    @Transactional
    public Telefono actualizar(Integer id, Telefono telefono) {
        Telefono existente = telefonoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Telefono no encontrado con ID: " + id));

        validarUsuario(telefono.getUsuario());

        existente.setTelefono(telefono.getTelefono());
        existente.setUsuario(telefono.getUsuario());

        return telefonoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!telefonoRepository.existsById(id)) {
            throw new IllegalArgumentException("Telefono no encontrado con ID: " + id);
        }
        telefonoRepository.deleteById(id);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getCodigoUsuario() == null) {
            throw new IllegalArgumentException("El telefono debe estar asociado a un usuario valido");
        }
        if (!usuarioRepository.existsById(usuario.getCodigoUsuario())) {
            throw new IllegalArgumentException("El usuario no existe");
        }
    }
}