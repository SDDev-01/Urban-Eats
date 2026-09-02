package com.urbaneats.service;

import com.urbaneats.entity.Usuario;
import com.urbaneats.dao.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario == null || usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (existePorCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Integer id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // Si cambia el correo, validar que no exista otro con ese correo
        if (!existente.getCorreo().equals(usuario.getCorreo()) && existePorCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }

        existente.setCorreo(usuario.getCorreo());
        existente.setNombres(usuario.getNombres());
        existente.setApellidos(usuario.getApellidos());

        // Solo actualiza password si se envía una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        // Nota: si existen teléfonos/direcciones asociados, la eliminación puede fallar por restricciones FK
        // a menos que se configure CascadeType.REMOVE/orphanRemoval en la entidad o se eliminen explícitamente.
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).isPresent();
    }
}