package com.urbaneats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.urbaneats.entity.Restaurante;
import com.urbaneats.service.IRestauranteService;

import lombok.RequiredArgsConstructor;

/**
 * Listado publico de cocinas.
 *
 * Nota: antes aqui estaba POST /restaurante, que creaba una cocina con 3 campos y la
 * publicaba al instante. Ahora la vinculacion pasa por una solicitud con documentos
 * sanitarios que revisa un tecnico (SolicitudCocinaController + servicio Python).
 */
@Controller
@RequiredArgsConstructor
public class RestauranteController {

    private final IRestauranteService restauranteService;

    /** Listado publico de cocinas activas (las suspendidas no se muestran). */
    @GetMapping("/restaurantes")
    public String listar(Model model) {
        var restaurantes = restauranteService.listarRestaurantes().stream()
                .filter(Restaurante::isActivo)
                .toList();
        model.addAttribute("restaurantes", restaurantes);
        model.addAttribute("horaActual", java.time.LocalTime.now());
        return "restaurantes";
    }
}
