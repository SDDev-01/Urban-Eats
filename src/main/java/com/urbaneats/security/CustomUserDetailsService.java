package com.urbaneats.security;

import com.urbaneats.entity.Usuario;
import com.urbaneats.dao.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
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