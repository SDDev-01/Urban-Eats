package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * Controlador del panel del gerente: menus, platos y alta rapida de plato.
 * Equivale a las rutas /perfilRestaurante y /perfilRestaurante/plato de Laravel.
 *
 * Nota: Laravel usaba PATCH y DELETE, pero un formulario HTML solo sabe GET y POST.
 * Por eso aqui las dos acciones van por POST con rutas propias.
 */
@Controller
@RequiredArgsConstructor
public class PerfilRestauranteController {

    private final IRestauranteService restauranteService;
    private final IMenuService menuService;
    private final IPlatoService platoService;

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

    /** Comprueba que el plato pertenezca a un menu del restaurante activo. */
    private boolean esDelRestaurante(Plato plato, Restaurante restaurante) {
        return plato != null
                && plato.getMenu() != null
                && plato.getMenu().getRestaurante() != null
                && plato.getMenu().getRestaurante().getCodigoRestaurante()
                        .equals(restaurante.getCodigoRestaurante());
    }

    /** Panel del restaurante: sus menus, y los platos de cada menu. */
    @GetMapping("/perfilRestaurante")
    public String mostrarPerfil(HttpSession session, Model model) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return "redirect:/seleccion-restaurante";
        }

        List<Menu> menus = menuService.listarMenusPorRestaurante(restaurante.getCodigoRestaurante());

        // LinkedHashMap para que la vista recorra los menus en el mismo orden que llegaron.
        Map<Menu, List<Plato>> platosPorMenu = new LinkedHashMap<>();
        for (Menu menu : menus) {
            platosPorMenu.put(menu, platoService.listarPlatosPorMenu(menu.getCodigoMenu()));
        }

        model.addAttribute("restaurante", restaurante);
        model.addAttribute("menus", menus);
        model.addAttribute("platosPorMenu", platosPorMenu);
        return "perfilRestaurante";
    }

    /** Alta rapida de plato dentro de uno de los menus del restaurante. */
    @PostMapping("/perfilRestaurante/plato")
    public String crearPlato(@RequestParam("CodigoMenu") Integer codigoMenu,
                             @RequestParam("Nombre") String nombre,
                             @RequestParam(name = "TipoComida", required = false) String tipoComida,
                             @RequestParam(name = "Precio", required = false) BigDecimal precio,
                             @RequestParam(name = "Disponibilidad", required = false) String disponibilidad,
                             @RequestParam(name = "Descripcion", required = false) String descripcion,
                             HttpSession session,
                             RedirectAttributes redirect) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return "redirect:/seleccion-restaurante";
        }

        Menu menu = menuService.buscarMenu(codigoMenu);

        // El menu tiene que existir y ser de este restaurante.
        if (menu == null || menu.getRestaurante() == null
                || !menu.getRestaurante().getCodigoRestaurante().equals(restaurante.getCodigoRestaurante())) {
            redirect.addFlashAttribute("error", "El menu seleccionado no es de este restaurante.");
            return "redirect:/perfilRestaurante";
        }

        Plato plato = new Plato();
        plato.setNombre(nombre);
        plato.setTipoComida(tipoComida);
        plato.setPrecio(precio);
        plato.setDisponibilidad(disponibilidad);
        plato.setDescripcion(descripcion);
        plato.setMenu(menu);

        platoService.guardarPlato(plato);

        redirect.addFlashAttribute("exito", "Plato \"" + nombre + "\" creado.");
        return "redirect:/perfilRestaurante";
    }

    /** Cambia solo la disponibilidad de un plato. */
    @PostMapping("/perfilRestaurante/plato/{id}/disponibilidad")
    public String actualizarDisponibilidad(@PathVariable("id") Integer id,
                                           @RequestParam("Disponibilidad") String disponibilidad,
                                           HttpSession session,
                                           RedirectAttributes redirect) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return "redirect:/seleccion-restaurante";
        }

        Plato plato = platoService.buscarPlato(id);
        if (!esDelRestaurante(plato, restaurante)) {
            redirect.addFlashAttribute("error", "Ese plato no es de este restaurante.");
            return "redirect:/perfilRestaurante";
        }

        plato.setDisponibilidad(disponibilidad);
        platoService.guardarPlato(plato);

        redirect.addFlashAttribute("exito", "Disponibilidad actualizada.");
        return "redirect:/perfilRestaurante";
    }

    /** Elimina un plato del restaurante activo. */
    @PostMapping("/perfilRestaurante/plato/{id}/eliminar")
    public String eliminarPlato(@PathVariable("id") Integer id,
                                HttpSession session,
                                RedirectAttributes redirect) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return "redirect:/seleccion-restaurante";
        }

        Plato plato = platoService.buscarPlato(id);
        if (!esDelRestaurante(plato, restaurante)) {
            redirect.addFlashAttribute("error", "Ese plato no es de este restaurante.");
            return "redirect:/perfilRestaurante";
        }

        platoService.eliminarPlato(id);

        redirect.addFlashAttribute("exito", "Plato eliminado.");
        return "redirect:/perfilRestaurante";
    }
}
