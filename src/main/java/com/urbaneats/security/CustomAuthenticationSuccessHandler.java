package com.urbaneats.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A donde va cada usuario despues de iniciar sesion.
 *   - Si tiene un rol de trabajo (cocina, repartidor o tecnico) elige como entrar en /seleccion-rol.
 *   - Si es solo cliente, vuelve a la pagina que intentaba abrir (por ejemplo /pago) o al catalogo.
 * Antes de eso limpia lo que haya dejado en la sesion otro usuario de este navegador.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Set<String> ROLES_DE_TRABAJO = Set.of("ROLE_GERENTE", "ROLE_REPARTIDOR", "ROLE_TECNICO");

    private static final List<String> ATRIBUTOS_DEL_USUARIO_ANTERIOR = List.of(
            RolActivo.ATRIBUTO, "restaurante_activo", RolActivo.DUENO_COCINA,
            "pedido_activo", "chat_historial");

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Spring conserva los atributos de la sesion al iniciar sesion. Si antes habia otro
        // usuario en este navegador, su cocina, su rol y su chat no deben pasar al nuevo.
        HttpSession sesion = request.getSession();
        for (String atributo : ATRIBUTOS_DEL_USUARIO_ANTERIOR) {
            sesion.removeAttribute(atributo);
        }

        boolean tieneRolDeTrabajo = authentication.getAuthorities().stream()
                .anyMatch(autoridad -> ROLES_DE_TRABAJO.contains(autoridad.getAuthority()));

        if (tieneRolDeTrabajo) {
            requestCache.removeRequest(request, response);
            response.sendRedirect(request.getContextPath() + "/seleccion-rol");
            return;
        }

        RolActivo.guardar(request, RolActivo.CLIENTE);
        SavedRequest pendiente = requestCache.getRequest(request, response);
        if (pendiente != null) {
            response.sendRedirect(pendiente.getRedirectUrl());
            return;
        }
        response.sendRedirect(request.getContextPath() + "/catalogo");
    }
}
