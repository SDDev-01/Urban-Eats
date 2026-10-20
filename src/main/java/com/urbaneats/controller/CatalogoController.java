package com.urbaneats.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.urbaneats.dao.AlergenoRepository;
import com.urbaneats.entity.Plato;
import com.urbaneats.service.IPlatoService;

/**
 * Controlador de la pantalla de catalogo.
 * Equivale a Route::get('/catalogo', [CatalogoController::class, 'mostrar']) de Laravel.
 */
@Controller
public class CatalogoController {

    private final IPlatoService platoService;
    private final AlergenoRepository alergenoRepository;

    /** Inyeccion por constructor: Spring entrega las dependencias al crear el controlador. */
    public CatalogoController(IPlatoService platoService, AlergenoRepository alergenoRepository) {
        this.platoService = platoService;
        this.alergenoRepository = alergenoRepository;
    }

    /** Muestra los platos de las cocinas activas y las categorias para los filtros. */
    @GetMapping("/catalogo")
    @Transactional(readOnly = true)
    public String mostrar(Model model) {
        List<Plato> platos = platoService.listarPlatosDeCocinasActivas();

        // Las categorias salen del menu de cada plato, sin repetir y en orden alfabetico.
        List<String> categorias = platos.stream()
                .map(plato -> plato.getMenu() != null ? plato.getMenu().getCategoria() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("platos", platos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("alergenosPorPlato", alergenosPorPlato(alergenoRepository));
        return "catalogo";
    }

    /**
     * Arma un mapa codigo de plato -> "Gluten, Lácteos" para mostrar los alergenos.
     * Lo reutiliza RestauranteDetalleController.
     */
    static Map<Integer, String> alergenosPorPlato(AlergenoRepository alergenoRepository) {
        return alergenoRepository.findAll().stream()
                .filter(alergeno -> alergeno.getPlato() != null && alergeno.getNombre() != null)
                .collect(Collectors.groupingBy(
                        alergeno -> alergeno.getPlato().getCodigoPlato(),
                        Collectors.mapping(alergeno -> alergeno.getNombre(), Collectors.joining(", "))));
    }
}
