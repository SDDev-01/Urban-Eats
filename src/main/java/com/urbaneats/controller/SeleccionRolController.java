package com.urbaneats.controller;

import com.urbaneats.entity.Gerente;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.dao.GerenteRepository;
import com.urbaneats.dao.RestauranteRepository;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Pantalla "¿Como deseas ingresar?" para usuarios con varios roles
 * (un mismo usuario puede ser cliente, cocina, repartidor o tecnico),
 * y la eleccion de cual de sus cocinas administrar.
 */
@Controller
@RequiredArgsConstructor
public class SeleccionRolController {

    private final GerenteRepository gerenteRepository;
    private final RestauranteRepository restauranteRepository;

    @GetMapping("/seleccion-rol")
    public String mostrarRol(Model model) {
        boolean esGerente = SesionHelper.tieneRol("GERENTE");
        boolean esRepartidor = SesionHelper.tieneRol("REPARTIDOR");
        boolean esTecnico = SesionHelper.tieneRol("TECNICO");

        if (!esGerente && !esRepartidor && !esTecnico) {
            return "redirect:/catalogo";
        }

        model.addAttribute("esGerente", esGerente);
        model.addAttribute("esRepartidor", esRepartidor);
        model.addAttribute("esTecnico", esTecnico);
        return "seleccionRol";
    }

    @GetMapping("/seleccion-restaurante")
    public String mostrarRestaurantes(Model model) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
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
        CustomUserDetails usuario = SesionHelper.usuarioActual();
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
