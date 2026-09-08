package com.urbaneats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pantallas que no necesitan datos del backend y solo devuelven su plantilla.
 * Equivale a los Route::view del web.php de Laravel.
 *
 * Sin estas rutas, los enlaces de la barra de navegacion terminaban en 404:
 * la plantilla existia en templates pero nadie la servia.
 */
@Controller
public class VistasController {

    /** Portada del sitio. */
    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    /** Carrito de compras. Su contenido se maneja en el navegador. */
    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }

    /** Mapa de cobertura. */
    @GetMapping("/mapa")
    public String mapa() {
        return "mapa";
    }

    /** Pantalla de cliente. */
    @GetMapping("/cliente")
    public String cliente() {
        return "cliente";
    }
}
