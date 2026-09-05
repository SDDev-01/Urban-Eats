package com.urbaneats.controller;

import com.urbaneats.entity.Gerente;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.dao.GerenteRepository;
import com.urbaneats.dao.RestauranteRepository;
import com.urbaneats.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SeleccionRolController {

    private final GerenteRepository gerenteRepository;
    private final RestauranteRepository restauranteRepository;

    private CustomUserDetails getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/seleccion-rol")
    public String mostrarRol(Model model, RedirectAttributes redirect) {
        CustomUserDetails usuario = getUsuarioActual();
        if (usuario == null) {
            return "redirect:/login";
        }

        Gerente gerente = gerenteRepository
            .findByUsuario_CodigoUsuario(usuario.getCodigoUsuario())
            .orElse(null);

        if (gerente == null) {
            return "redirect:/catalogo";
        }

        return "seleccionRol";
    }

    @GetMapping("/seleccion-restaurante")
    public String mostrarRestaurantes(Model model, RedirectAttributes redirect) {
        CustomUserDetails usuario = getUsuarioActual();
        if (usuario == null) {
            return "redirect:/login";
        }

        Gerente gerente = gerenteRepository
            .findByUsuario_CodigoUsuario(usuario.getCodigoUsuario())
            .orElse(null);

        if (gerente == null) {
            return "redirect:/catalogo";
        }

        List<Restaurante> restaurantes = restauranteRepository
            .findByGerente_CodigoGerente(gerente.getCodigoGerente());

        model.addAttribute("restaurantes", restaurantes);
        return "seleccionRestaurante";
    }

    @PostMapping("/seleccion-restaurante/{id}")
    public String confirmarRestaurante(@PathVariable Integer id,
                                       HttpSession session,
                                       RedirectAttributes redirect) {
        CustomUserDetails usuario = getUsuarioActual();
        if (usuario == null) {
            return "redirect:/login";
        }

        Gerente gerente = gerenteRepository
            .findByUsuario_CodigoUsuario(usuario.getCodigoUsuario())
            .orElse(null);

        if (gerente == null) {
            return "redirect:/catalogo";
        }

        boolean esSuRestaurante = restauranteRepository
            .existsByGerente_CodigoGerenteAndCodigoRestaurante(gerente.getCodigoGerente(), id);

        if (!esSuRestaurante) {
            redirect.addFlashAttribute("error", "Restaurante no valido");
            return "redirect:/seleccion-restaurante";
        }

        session.setAttribute("restaurante_activo", id);
        return "redirect:/perfilRestaurante";
    }
}