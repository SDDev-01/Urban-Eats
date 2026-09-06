package com.urbaneats.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbaneats.dao.GerenteRepository;
import com.urbaneats.entity.Ciudad;
import com.urbaneats.entity.Gerente;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.service.ICiudadService;
import com.urbaneats.service.IRestauranteService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador de las pantallas de restaurante.
 * Equivale a las rutas /restaurantes, GET /restaurante y POST /restaurante de Laravel.
 */
@Controller
@RequiredArgsConstructor
public class RestauranteController {

    private final IRestauranteService restauranteService;
    private final ICiudadService ciudadService;
    private final GerenteRepository gerenteRepository;

    /** Devuelve el usuario que inicio sesion, o null si no hay sesion. */
    private CustomUserDetails getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    /** Listado publico de restaurantes. */
    @GetMapping("/restaurantes")
    public String listar(Model model) {
        model.addAttribute("restaurantes", restauranteService.listarRestaurantes());
        return "restaurantes";
    }

    /** Formulario de creacion de restaurante. Solo para gerentes. */
    @GetMapping("/restaurante")
    public String mostrarPagina(Model model) {
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

        // El formulario necesita la lista de ciudades porque Restaurante.ciudad es obligatoria.
        model.addAttribute("ciudades", ciudadService.listarCiudades());
        return "restaurante";
    }

    /** Guarda el restaurante nuevo y lo deja asociado al gerente que inicio sesion. */
    @PostMapping("/restaurante")
    public String crearRestaurante(@RequestParam("Nombre") String nombre,
                                   @RequestParam("Direccion") String direccion,
                                   @RequestParam("Horario") String horario,
                                   @RequestParam("codigoCiudad") Integer codigoCiudad,
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

        Ciudad ciudad = ciudadService.buscarCiudad(codigoCiudad);
        if (ciudad == null) {
            redirect.addFlashAttribute("error", "La ciudad seleccionada no existe.");
            return "redirect:/restaurante";
        }

        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(nombre);
        restaurante.setDireccion(direccion);
        restaurante.setHorario(horario);
        restaurante.setCiudad(ciudad);
        restaurante.setGerente(gerente);

        restauranteService.guardarRestaurante(restaurante);

        redirect.addFlashAttribute("exito", "Restaurante creado correctamente.");
        return "redirect:/seleccion-restaurante";
    }
}
