package com.urbaneats.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.urbaneats.dao.PagoRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.Menu;
import com.urbaneats.entity.Pago;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.entity.Telefono;
import com.urbaneats.entity.Usuario;
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

    private static final Map<String, String> ESTADO_A_JS = Map.of(
            "En Proceso", "nuevo",
            "Entregado", "entregado",
            "Cancelado", "rechazado"
    );

    private final IRestauranteService restauranteService;
    private final IMenuService menuService;
    private final IPlatoService platoService;
    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;

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

        model.addAttribute("pedidosJson", pedidosDeHoy(restaurante));
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

    /** Arma la lista que perfil-restaurante.js espera en window.PEDIDOS_BD, con los pedidos de hoy. */
    private List<PedidoDashboardItem> pedidosDeHoy(Restaurante restaurante) {
        List<Pedido> pedidos = pedidoRepository.findByRestaurante_CodigoRestauranteAndFechaPedido(
                restaurante.getCodigoRestaurante(), LocalDate.now());

        return pedidos.stream().map(this::aDashboardItem).toList();
    }

    private PedidoDashboardItem aDashboardItem(Pedido pedido) {
        Envio envio = pedido.getEnvio();
        Usuario usuario = envio != null && envio.getCliente() != null ? envio.getCliente().getUsuario() : null;
        Pago pago = envio != null
                ? pagoRepository.findByEnvio_CodigoEnvio(envio.getCodigoEnvio()).orElse(null)
                : null;

        String cliente = usuario != null
                ? (nullASinValor(usuario.getNombres()) + " " + nullASinValor(usuario.getApellidos())).trim()
                : "—";
        String direccion = usuario != null && usuario.getDirecciones() != null && !usuario.getDirecciones().isEmpty()
                ? primeraDireccion(usuario) : "—";
        String telefono = usuario != null && usuario.getTelefonos() != null && !usuario.getTelefonos().isEmpty()
                ? primerTelefono(usuario) : "—";
        String hora = envio != null && envio.getHoraEntrega() != null
                ? envio.getHoraEntrega().format(DateTimeFormatter.ofPattern("HH:mm")) : "—";
        BigDecimal total = pago != null && pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO;
        String estado = ESTADO_A_JS.getOrDefault(pedido.getEstado(), "nuevo");
        String descripcion = envio != null && envio.getDescripcion() != null ? envio.getDescripcion() : "Sin descripción";

        return new PedidoDashboardItem(
                "P-" + String.format("%03d", pedido.getCodigoPedido()),
                cliente.isBlank() ? "—" : cliente,
                direccion,
                telefono,
                List.of(descripcion),
                total,
                estado,
                hora
        );
    }

    private String primeraDireccion(Usuario usuario) {
        Direccion direccion = usuario.getDirecciones().get(0);
        return direccion.getDireccion() != null ? direccion.getDireccion() : "—";
    }

    private String primerTelefono(Usuario usuario) {
        Telefono telefono = usuario.getTelefonos().get(0);
        return telefono.getTelefono() != null ? telefono.getTelefono() : "—";
    }

    private String nullASinValor(String valor) {
        return valor != null ? valor : "";
    }
}
