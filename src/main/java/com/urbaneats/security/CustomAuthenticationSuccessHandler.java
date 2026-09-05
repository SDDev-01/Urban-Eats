package com.urbaneats.security;

import com.urbaneats.dao.GerenteRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GerenteRepository gerenteRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        boolean esGerente = gerenteRepository
            .findByUsuario_CodigoUsuario(userDetails.getCodigoUsuario())
            .isPresent();

        if (esGerente) {
            response.sendRedirect(request.getContextPath() + "/seleccion-rol");
        } else {
            response.sendRedirect(request.getContextPath() + "/catalogo");
        }
    }
}