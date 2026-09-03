package com.urbaneats.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.urbaneats.entity.Plato;
import com.urbaneats.service.IPlatoService;

/**
 * Controlador de la pantalla de catalogo.
 * Equivale a Route::get('/catalogo', [CatalogoController::class, 'mostrar']) de Laravel.
 */
@Controller
public class CatalogoController {

    private final IPlatoService platoService;

    /** Inyeccion por constructor: Spring entrega el servicio al crear el controlador. */
    public CatalogoController(IPlatoService platoService) {
        this.platoService = platoService;
    }

    /** Muestra todos los platos del catalogo y las categorias para los filtros. */
    @GetMapping("/catalogo")
    public String mostrar(Model model) {
        List<Plato> platos = platoService.listarPlatos();

        // Las categorias salen del menu de cada plato, sin repetir y en orden alfabetico.
        List<String> categorias = platos.stream()
                .map(plato -> plato.getMenu() != null ? plato.getMenu().getCategoria() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("platos", platos);
        model.addAttribute("categorias", categorias);
        return "catalogo";
    }
}
