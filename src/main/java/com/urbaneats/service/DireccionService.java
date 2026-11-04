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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DireccionService implements IDireccionService {

    private static final double LATITUD_MIN = 3.7, LATITUD_MAX = 4.9;
    private static final double LONGITUD_MIN = -74.5, LONGITUD_MAX = -73.9;

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
        if (direccion.tieneCoordenadas()) {
            validarEnBogota(direccion.getLatitud(), direccion.getLongitud());
        }
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
        if (direccion.tieneCoordenadas()) {
            validarEnBogota(direccion.getLatitud(), direccion.getLongitud());
            existente.setLatitud(direccion.getLatitud());
            existente.setLongitud(direccion.getLongitud());
        }

        return direccionRepository.save(existente);
    }

    @Override
    @Transactional
    public Direccion actualizarUbicacion(Integer id, Integer codigoUsuario, BigDecimal latitud, BigDecimal longitud) {
        Direccion direccion = direccionRepository.findById(id)
            .filter(d -> d.getUsuario() != null && d.getUsuario().getCodigoUsuario().equals(codigoUsuario))
            .orElseThrow(() -> new ReglaNegocioException("Esa dirección no es tuya."));
        validarEnBogota(latitud, longitud);
        direccion.setLatitud(latitud);
        direccion.setLongitud(longitud);
        return direccionRepository.save(direccion);
    }

    /** Las entregas son en Bogota (con Sumapaz): un punto fuera es casi siempre un clic equivocado. */
    private void validarEnBogota(BigDecimal latitud, BigDecimal longitud) {
        if (latitud == null || longitud == null
                || latitud.doubleValue() < LATITUD_MIN || latitud.doubleValue() > LATITUD_MAX
                || longitud.doubleValue() < LONGITUD_MIN || longitud.doubleValue() > LONGITUD_MAX) {
            throw new ReglaNegocioException("El punto debe quedar dentro de Bogotá.");
        }
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