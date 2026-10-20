package com.urbaneats.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

/**
 * Utilidades de la sesion de Spring Security.
 *
 * usuarioActual(): el usuario que inicio sesion, o null.
 * recargarRoles(): vuelve a leer los roles de la base y los pone en la sesion.
 *   Hace falta cuando un usuario gana un rol mientras esta conectado (por ejemplo,
 *   al registrarse como repartidor); sin esto tendria que cerrar sesion y volver a entrar
 *   para que Spring Security le deje abrir las paginas del nuevo rol.
 */
@Component
public class SesionHelper {

    private final CustomUserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public SesionHelper(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /** Devuelve el usuario que inicio sesion, o null si no hay sesion. */
    public static CustomUserDetails usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails usuario) {
            return usuario;
        }
        return null;
    }

    /** True si el usuario de la sesion tiene ese rol, por ejemplo "GERENTE". */
    public static boolean tieneRol(String rol) {
        CustomUserDetails usuario = usuarioActual();
        return usuario != null && usuario.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_" + rol));
    }

    /** Lee otra vez los roles desde la base y actualiza la sesion actual. */
    public void recargarRoles(HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetails actual = usuarioActual();
        if (actual == null) {
            return;
        }
        UserDetails recargado = userDetailsService.loadUserByUsername(actual.getUsername());
        UsernamePasswordAuthenticationToken nuevaAutenticacion =
                new UsernamePasswordAuthenticationToken(recargado, null, recargado.getAuthorities());

        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(nuevaAutenticacion);
        SecurityContextHolder.setContext(contexto);
        securityContextRepository.saveContext(contexto, request, response);
    }
}
