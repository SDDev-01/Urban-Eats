package com.urbaneats.service;

import com.urbaneats.entity.Rol;
import com.urbaneats.dao.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolService implements IRolService {

    private final RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Rol> listarTodos(Pageable pageable) {
        return rolRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorId(Integer id) {
        return rolRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorNombre(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol);
    }

    @Override
    @Transactional
    public Rol guardar(Rol rol) {
        if (rol == null || rol.getNombreRol() == null || rol.getNombreRol().isBlank()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio");
        }
        if (existePorNombre(rol.getNombreRol())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + rol.getNombreRol());
        }
        return rolRepository.save(rol);
    }

    @Override
    @Transactional
    public Rol actualizar(Integer id, Rol rol) {
        Rol existente = rolRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));

        if (!existente.getNombreRol().equals(rol.getNombreRol()) && existePorNombre(rol.getNombreRol())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + rol.getNombreRol());
        }

        existente.setNombreRol(rol.getNombreRol());
        existente.setDescripcionRol(rol.getDescripcionRol());

        return rolRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new IllegalArgumentException("Rol no encontrado con ID: " + id);
        }
        rolRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol).isPresent();
    }
}