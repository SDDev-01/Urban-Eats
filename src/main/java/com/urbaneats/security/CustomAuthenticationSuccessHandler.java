package com.urbaneats.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * A donde va cada usuario despues de iniciar sesion.
 *   - Si tiene un rol de trabajo (cocina, repartidor o tecnico) elige como entrar en /seleccion-rol.
 *   - Si es solo cliente, vuelve a la pagina que intentaba abrir (por ejemplo /pago) o al catalogo.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Set<String> ROLES_DE_TRABAJO = Set.of("ROLE_GERENTE", "ROLE_REPARTIDOR", "ROLE_TECNICO");

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        boolean tieneRolDeTrabajo = authentication.getAuthorities().stream()
                .anyMatch(autoridad -> ROLES_DE_TRABAJO.contains(autoridad.getAuthority()));

        if (tieneRolDeTrabajo) {
            response.sendRedirect(request.getContextPath() + "/seleccion-rol");
            return;
        }

        SavedRequest pendiente = requestCache.getRequest(request, response);
        if (pendiente != null) {
            response.sendRedirect(pendiente.getRedirectUrl());
            return;
        }
        response.sendRedirect(request.getContextPath() + "/catalogo");
    }
}
