package com.urbaneats.controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.urbaneats.entity.Menu;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.service.IMenuService;
import com.urbaneats.service.IPlatoService;
import com.urbaneats.service.IRestauranteService;

/**
 * Controlador de la pantalla de detalle de un restaurante.
 * Equivale a Route::get('/restauranteDetalle', [RestauranteDetalleController::class, 'mostrarDetalle']).
 */
@Controller
public class RestauranteDetalleController {

    private final IRestauranteService restauranteService;
    private final IMenuService menuService;
    private final IPlatoService platoService;

    /** Inyeccion por constructor: Spring entrega los tres servicios al crear el controlador. */
    public RestauranteDetalleController(IRestauranteService restauranteService,
                                        IMenuService menuService,
                                        IPlatoService platoService) {
        this.restauranteService = restauranteService;
        this.menuService = menuService;
        this.platoService = platoService;
    }

    /**
     * Muestra un restaurante y sus platos.
     * Los platos se arman aqui porque van dos saltos: Plato -> Menu -> Restaurante.
     * Primero se piden los menus del restaurante, y luego los platos de esos menus.
     */
    @GetMapping("/restauranteDetalle")
    public String mostrarDetalle(@RequestParam("id") Integer id, Model model) {
        Restaurante restaurante = restauranteService.buscarRestaurante(id);

        // Si el codigo no existe, se devuelve al listado en vez de mostrar una pagina vacia.
        if (restaurante == null) {
            return "redirect:/restaurantes";
        }

        List<Menu> menus = menuService.listarMenusPorRestaurante(id);
        Set<Integer> codigosMenu = menus.stream()
                .map(Menu::getCodigoMenu)
                .collect(Collectors.toSet());

        List<Plato> platos = platoService.listarPlatos().stream()
                .filter(plato -> plato.getMenu() != null
                        && codigosMenu.contains(plato.getMenu().getCodigoMenu()))
                .toList();

        List<String> categorias = platos.stream()
                .map(plato -> plato.getMenu() != null ? plato.getMenu().getCategoria() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("restaurante", restaurante);
        model.addAttribute("platos", platos);
        model.addAttribute("categorias", categorias);
        return "restauranteDetalle";
    }
}
