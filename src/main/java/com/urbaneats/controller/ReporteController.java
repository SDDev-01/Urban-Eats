package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.urbaneats.entity.Plato;
import com.urbaneats.service.IPlatoService;
import com.urbaneats.service.IRestauranteService;

import lombok.RequiredArgsConstructor;

/**
 * Reporte de platos del catalogo con filtros multicriterio.
 * El formulario viaja por GET, de modo que los filtros quedan en la URL
 * y el reporte se puede compartir o volver a abrir tal como se consulto.
 */
@Controller
@RequiredArgsConstructor
public class ReporteController {

    private final IPlatoService platoService;
    private final IRestauranteService restauranteService;

    @GetMapping("/reportes")
    public String mostrar(@RequestParam(required = false) Integer codigoRestaurante,
                          @RequestParam(required = false) String categoria,
                          @RequestParam(required = false) String disponibilidad,
                          @RequestParam(required = false) BigDecimal precioMinimo,
                          @RequestParam(required = false) BigDecimal precioMaximo,
                          Model model) {

        List<Plato> resultados = platoService.listarPlatosFiltrados(
                codigoRestaurante, categoria, disponibilidad, precioMinimo, precioMaximo);

        // Opciones de los desplegables, tomadas de los datos que existen hoy.
        List<String> categorias = platoService.listarPlatos().stream()
                .map(plato -> plato.getMenu() != null ? plato.getMenu().getCategoria() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        List<String> disponibilidades = platoService.listarPlatos().stream()
                .map(Plato::getDisponibilidad)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("restaurantes", restauranteService.listarRestaurantes());
        model.addAttribute("categorias", categorias);
        model.addAttribute("disponibilidades", disponibilidades);
        model.addAttribute("resultados", resultados);

        // Indicadores del reporte.
        model.addAttribute("totalResultados", resultados.size());
        model.addAttribute("precioPromedio", promedio(resultados));
        model.addAttribute("precioMenor", extremo(resultados, true));
        model.addAttribute("precioMayor", extremo(resultados, false));

        // Se devuelven al formulario para que conserve lo que el usuario eligio.
        model.addAttribute("filtroRestaurante", codigoRestaurante);
        model.addAttribute("filtroCategoria", categoria);
        model.addAttribute("filtroDisponibilidad", disponibilidad);
        model.addAttribute("filtroPrecioMinimo", precioMinimo);
        model.addAttribute("filtroPrecioMaximo", precioMaximo);

        return "reportes";
    }

    /** Precio promedio de los platos del reporte, o null si no hay resultados. */
    private BigDecimal promedio(List<Plato> platos) {
        List<BigDecimal> precios = precios(platos);
        if (precios.isEmpty()) {
            return null;
        }
        BigDecimal suma = precios.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(precios.size()), 0, java.math.RoundingMode.HALF_UP);
    }

    /** Precio menor o mayor del reporte, segun se pida. */
    private BigDecimal extremo(List<Plato> platos, boolean menor) {
        List<BigDecimal> precios = precios(platos);
        if (precios.isEmpty()) {
            return null;
        }
        Comparator<BigDecimal> orden = Comparator.naturalOrder();
        return menor ? precios.stream().min(orden).get() : precios.stream().max(orden).get();
    }

    private List<BigDecimal> precios(List<Plato> platos) {
        return platos.stream().map(Plato::getPrecio).filter(Objects::nonNull).toList();
    }
}
