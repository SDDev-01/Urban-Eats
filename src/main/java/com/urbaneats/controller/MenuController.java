package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbaneats.entity.Menu;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.service.IMenuService;
import com.urbaneats.service.IPlatoService;
import com.urbaneats.service.IRestauranteService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador de la pantalla de creacion de menu.
 * Equivale a las rutas GET /menu y POST /menu de Laravel.
 * Trabaja sobre el restaurante que el gerente eligio en /seleccion-restaurante.
 */
@Controller
@RequiredArgsConstructor
public class MenuController {

    private final IMenuService menuService;
    private final IPlatoService platoService;
    private final IRestauranteService restauranteService;

    /**
     * Lee de la sesion el restaurante activo que dejo SeleccionRolController.
     * Devuelve null si no hay ninguno elegido o si el codigo ya no existe.
     */
    private Restaurante getRestauranteActivo(HttpSession session) {
        Object codigo = session.getAttribute("restaurante_activo");
        if (!(codigo instanceof Integer)) {
            return null;
        }
        return restauranteService.buscarRestaurante((Integer) codigo);
    }

    /** Formulario para crear una categoria de menu con sus platos. */
    @GetMapping("/menu")
    public String mostrarFormulario(HttpSession session, Model model) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return "redirect:/seleccion-restaurante";
        }

        model.addAttribute("restaurante", restaurante);
        return "menu";
    }

    /**
     * Crea el menu y, si vinieron platos en el formulario, los guarda enseguida.
     * Los platos llegan como listas paralelas: la posicion i de cada arreglo es un plato.
     */
    @PostMapping("/menu")
    public String crearMenu(@RequestParam("Categoria") String categoria,
                            @RequestParam(name = "platoNombre", required = false) List<String> nombres,
                            @RequestParam(name = "platoTipoComida", required = false) List<String> tipos,
                            @RequestParam(name = "platoPrecio", required = false) List<BigDecimal> precios,
                            @RequestParam(name = "platoDisponibilidad", required = false) List<String> disponibilidades,
                            @RequestParam(name = "platoDescripcion", required = false) List<String> descripciones,
                            HttpSession session,
                            RedirectAttributes redirect) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return "redirect:/seleccion-restaurante";
        }

        Menu menu = new Menu();
        menu.setCategoria(categoria);
        menu.setRestaurante(restaurante);
        Menu menuGuardado = menuService.guardarMenu(menu);

        int creados = 0;
        if (nombres != null) {
            for (int i = 0; i < nombres.size(); i++) {
                String nombre = nombres.get(i);

                // Las filas vacias del formulario se ignoran.
                if (nombre == null || nombre.isBlank()) {
                    continue;
                }

                Plato plato = new Plato();
                plato.setNombre(nombre);
                plato.setTipoComida(valorEn(tipos, i));
                plato.setDescripcion(valorEn(descripciones, i));
                plato.setDisponibilidad(valorEn(disponibilidades, i));
                plato.setPrecio(precios != null && i < precios.size() ? precios.get(i) : null);
                plato.setMenu(menuGuardado);

                platoService.guardarPlato(plato);
                creados++;
            }
        }

        redirect.addFlashAttribute("exito",
                "Menu \"" + categoria + "\" creado con " + creados + " plato(s).");
        return "redirect:/perfilRestaurante";
    }

    /** Devuelve el elemento i de la lista, o null si la lista no llega tan lejos. */
    private String valorEn(List<String> lista, int i) {
        return lista != null && i < lista.size() ? lista.get(i) : null;
    }
}
