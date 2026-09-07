package com.urbaneats.security;

import com.urbaneats.entity.Usuario;
import com.urbaneats.dao.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * La transaccion es obligatoria: Usuario.roles es @ManyToMany, que carga de forma
     * perezosa. El login ocurre dentro del filtro de seguridad, antes de que Spring abra
     * la sesion de la peticion, asi que sin ella leer los roles lanza
     * LazyInitializationException y la autenticacion falla como si la clave fuera mala.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        var authorities = usuario.getRoles().stream()
            .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().toUpperCase()))
            .collect(Collectors.toList());

        return new CustomUserDetails(
            usuario.getCodigoUsuario(),
            usuario.getNombres(),
            usuario.getCorreo(),
            usuario.getPassword(),
            authorities
        );
    }
}