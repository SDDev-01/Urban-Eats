package com.urbaneats.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbaneats.dao.GerenteRepository;
import com.urbaneats.dao.RolRepository;
import com.urbaneats.dao.UsuarioRepository;
import com.urbaneats.entity.Ciudad;
import com.urbaneats.entity.Gerente;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.entity.Rol;
import com.urbaneats.entity.Usuario;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.service.ICiudadService;
import com.urbaneats.service.IRestauranteService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador de las pantallas de restaurante.
 * Equivale a las rutas /restaurantes, GET /restaurante y POST /restaurante de Laravel.
 *
 * Nota: crear un restaurante es, en Laravel, la forma de "convertirse en gerente": no
 * hace falta ya serlo. Por eso crearRestaurante crea el Gerente sobre la marcha
 * (firstOrCreate) en vez de exigirlo de antemano.
 */
@Controller
@RequiredArgsConstructor
public class RestauranteController {

    /** Ciudad fija con la que Laravel crea todo restaurante nuevo (no hay selector en el formulario). */
    private static final Integer CODIGO_CIUDAD_DEFECTO = 1;
    private static final String NOMBRE_ROL_GERENTE = "Gerente";

    private final IRestauranteService restauranteService;
    private final ICiudadService ciudadService;
    private final GerenteRepository gerenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

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
        var restaurantesJson = restauranteService.listarRestaurantes().stream()
                .map(r -> new RestauranteResumen(
                        r.getCodigoRestaurante(), r.getNombre(), r.getDireccion(), r.getHorario(), null))
                .toList();
        model.addAttribute("restaurantesJson", restaurantesJson);
        return "restaurantes";
    }

    /** Formulario de creacion de restaurante. Cualquier usuario logueado puede crear el suyo. */
    @GetMapping("/restaurante")
    public String mostrarPagina() {
        return "restaurante";
    }

    /**
     * Guarda el restaurante nuevo. Si el usuario todavia no tiene un Gerente asociado,
     * lo crea aqui mismo, y le asigna el rol "Gerente" si no lo tenia.
     */
    @PostMapping("/restaurante")
    @Transactional
    public String crearRestaurante(@RequestParam("Nombre") String nombre,
                                   @RequestParam("Direccion") String direccion,
                                   @RequestParam("Horario") String horario,
                                   RedirectAttributes redirect) {
        CustomUserDetails usuarioActual = getUsuarioActual();

        Ciudad ciudad = ciudadService.buscarCiudad(CODIGO_CIUDAD_DEFECTO);
        if (ciudad == null) {
            redirect.addFlashAttribute("error", "No se pudo determinar la ciudad del restaurante.");
            return "redirect:/restaurante";
        }

        Gerente gerente = gerenteRepository.findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .orElseGet(() -> {
                    Gerente nuevo = new Gerente();
                    nuevo.setUsuario(usuarioRepository.getReferenceById(usuarioActual.getCodigoUsuario()));
                    return gerenteRepository.save(nuevo);
                });

        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(nombre);
        restaurante.setDireccion(direccion);
        restaurante.setHorario(horario);
        restaurante.setCiudad(ciudad);
        restaurante.setGerente(gerente);
        restauranteService.guardarRestaurante(restaurante);

        asignarRolGerenteSiFalta(usuarioActual.getCodigoUsuario());

        redirect.addFlashAttribute("exito", "¡Restaurante creado exitosamente!");
        return "redirect:/perfilRestaurante";
    }

    private void asignarRolGerenteSiFalta(Integer codigoUsuario) {
        Usuario usuario = usuarioRepository.findById(codigoUsuario).orElseThrow();
        boolean yaLoTiene = usuario.getRoles().stream()
                .anyMatch(rol -> NOMBRE_ROL_GERENTE.equals(rol.getNombreRol()));
        if (yaLoTiene) {
            return;
        }

        Rol rolGerente = rolRepository.findByNombreRol(NOMBRE_ROL_GERENTE).orElseThrow();
        usuario.getRoles().add(rolGerente);
        usuarioRepository.save(usuario);
    }
}
