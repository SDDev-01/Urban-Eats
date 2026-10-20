package com.urbaneats.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.EstadoPedido;
import com.urbaneats.entity.Menu;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.entity.Telefono;
import com.urbaneats.entity.Usuario;
import com.urbaneats.security.SesionHelper;
import com.urbaneats.service.IMenuService;
import com.urbaneats.service.IPlatoService;
import com.urbaneats.service.IRestauranteService;
import com.urbaneats.service.PedidoEstadoService;
import com.urbaneats.service.ReglaNegocioException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador del panel de la cocina (gerente): pedidos, menus y platos.
 *
 * Pedidos (JSON, los usa perfil-restaurante.js):
 *   GET  /perfilRestaurante/pedidos             pedidos activos + finalizados de hoy
 *   POST /perfilRestaurante/pedido/{id}/estado  {accion: aceptar | rechazar | listo, motivo}
 *   POST /perfilRestaurante/acepta-pedidos      {acepta: true | false} (boton Abierto/Cerrado)
 *
 * Nota: Laravel usaba PATCH y DELETE, pero un formulario HTML solo sabe GET y POST.
 * Por eso las acciones de platos van por POST con rutas propias.
 */
@Controller
@RequiredArgsConstructor
public class PerfilRestauranteController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("hh:mm a");

    /** Estados que la cocina sigue viendo aunque el pedido no sea de hoy. */
    private static final EnumSet<EstadoPedido> ACTIVOS = EnumSet.of(EstadoPedido.SOLICITADO,
            EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO_PARA_ENVIO, EstadoPedido.EN_CAMINO);

    private final IRestauranteService restauranteService;
    private final IMenuService menuService;
    private final IPlatoService platoService;
    private final PedidoRepository pedidoRepository;
    private final PedidoEstadoService pedidoEstadoService;

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

    /** Panel del restaurante: sus menus, y los platos de cada menu. Los pedidos llegan por JSON. */
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

    // ===================== PEDIDOS (JSON) =====================

    @GetMapping("/perfilRestaurante/pedidos")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> pedidos(HttpSession session) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return ResponseEntity.status(409).body(Map.of("error", "Primero elige una de tus cocinas."));
        }
        Integer codigo = restaurante.getCodigoRestaurante();

        // Activos (de cualquier dia) + los finalizados de hoy, sin repetir.
        List<Pedido> lista = new ArrayList<>(pedidoRepository
                .findByRestaurante_CodigoRestauranteAndEstadoInOrderByCodigoPedidoDesc(codigo, ACTIVOS));
        pedidoRepository.findByRestaurante_CodigoRestauranteAndFechaPedido(codigo, LocalDate.now()).stream()
                .filter(pedido -> pedido.getEstado().esFinal())
                .forEach(lista::add);

        return ResponseEntity.ok(lista.stream().map(this::aDashboardItem).toList());
    }

    @PostMapping("/perfilRestaurante/pedido/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable("id") Integer id,
                                                 @RequestBody Map<String, String> cuerpo,
                                                 HttpSession session) {
        Restaurante restaurante = getRestauranteActivo(session);
        Integer codigoUsuario = SesionHelper.usuarioActual().getCodigoUsuario();
        String accion = cuerpo.getOrDefault("accion", "");
        try {
            Pedido pedido = switch (accion) {
                case "aceptar" -> pedidoEstadoService.aceptar(id, restaurante, codigoUsuario);
                case "rechazar" -> pedidoEstadoService.rechazar(id, restaurante, codigoUsuario, cuerpo.get("motivo"));
                case "listo" -> pedidoEstadoService.marcarListo(id, restaurante, codigoUsuario);
                default -> throw new ReglaNegocioException("Acción no válida: " + accion);
            };
            return ResponseEntity.ok(Map.of("ok", true, "estado", pedido.getEstado().getEtiqueta()));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    /** Boton Abierto/Cerrado: pausa o reanuda la recepcion de pedidos. Ahora si se guarda. */
    @PostMapping("/perfilRestaurante/acepta-pedidos")
    @ResponseBody
    public ResponseEntity<?> aceptaPedidos(@RequestBody Map<String, Boolean> cuerpo, HttpSession session) {
        Restaurante restaurante = getRestauranteActivo(session);
        if (restaurante == null) {
            return ResponseEntity.status(409).body(Map.of("error", "Primero elige una de tus cocinas."));
        }
        restaurante.setAceptaPedidos(Boolean.TRUE.equals(cuerpo.get("acepta")));
        restauranteService.guardarRestaurante(restaurante);
        return ResponseEntity.ok(Map.of("acepta", restaurante.isAceptaPedidos()));
    }

    // ===================== PLATOS =====================

    /** Alta rapida de plato dentro de uno de los menus del restaurante. */
    @PostMapping("/perfilRestaurante/plato")
    public String crearPlato(@RequestParam("CodigoMenu") Integer codigoMenu,
                             @RequestParam("Nombre") String nombre,
                             @RequestParam(name = "TipoComida", required = false) String tipoComida,
                             @RequestParam(name = "Precio", required = false) BigDecimal precio,
                             @RequestParam(name = "Disponibilidad", required = false) String disponibilidad,
                             @RequestParam(name = "Descripcion", required = false) String descripcion,
                             @RequestParam(name = "Calorias", required = false) Integer calorias,
                             @RequestParam(name = "Etiquetas", required = false) List<String> etiquetas,
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

        // Validaciones del servidor: el formulario del navegador se puede saltar.
        if (nombre == null || nombre.trim().length() < 3) {
            redirect.addFlashAttribute("error", "El nombre del plato debe tener al menos 3 caracteres.");
            return "redirect:/perfilRestaurante";
        }
        if (precio == null || precio.compareTo(BigDecimal.valueOf(1000)) < 0
                || precio.compareTo(BigDecimal.valueOf(500000)) > 0) {
            redirect.addFlashAttribute("error", "El precio debe estar entre $1.000 y $500.000.");
            return "redirect:/perfilRestaurante";
        }
        if (calorias != null && (calorias < 0 || calorias > 5000)) {
            redirect.addFlashAttribute("error", "Las calorías deben estar entre 0 y 5.000.");
            return "redirect:/perfilRestaurante";
        }

        Plato plato = new Plato();
        plato.setNombre(nombre.trim());
        plato.setTipoComida(tipoComida);
        plato.setPrecio(precio);
        plato.setDisponibilidad(disponibilidad != null ? disponibilidad : Plato.DISPONIBLE);
        plato.setDescripcion(descripcion);
        plato.setCalorias(calorias);
        plato.setEtiquetas(etiquetas == null ? null : String.join(",", etiquetas));
        plato.setMenu(menu);

        platoService.guardarPlato(plato);

        redirect.addFlashAttribute("exito", "Plato \"" + plato.getNombre() + "\" creado.");
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

    /**
     * Elimina un plato del restaurante activo.
     * Si el plato ya aparece en pedidos, la base no deja borrarlo (llave foranea):
     * en ese caso se marca como "No disponible" y se le explica al gerente.
     */
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

        try {
            platoService.eliminarPlato(id);
            redirect.addFlashAttribute("exito", "Plato eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            plato.setDisponibilidad("No disponible");
            platoService.guardarPlato(plato);
            redirect.addFlashAttribute("error", "\"" + plato.getNombre()
                    + "\" ya tiene pedidos u opiniones, así que no se puede borrar: quedó como No disponible.");
        }
        return "redirect:/perfilRestaurante";
    }

    // ===================== AUXILIARES =====================

    private PedidoDashboardItem aDashboardItem(Pedido pedido) {
        Envio envio = pedido.getEnvio();
        Usuario usuario = envio != null && envio.getCliente() != null ? envio.getCliente().getUsuario() : null;

        String cliente = usuario != null ? usuario.getNombreCompleto() : "—";
        Direccion direccionEntrega = envio != null ? envio.getDireccion() : null;
        String direccion = direccionEntrega != null ? direccionEntrega.getDireccion() : primeraDireccion(usuario);
        String telefono = primerTelefono(usuario);
        String hora = pedido.getFechaHora() != null ? pedido.getFechaHora().format(HORA) : "—";
        String repartidor = envio != null && envio.getRepartidor() != null
                ? envio.getRepartidor().getUsuario().getNombreCompleto() : null;

        List<String> items = pedido.getDetalles().stream()
                .map(detalle -> detalle.getCantidad() + "× " + detalle.getPlato().getNombre())
                .toList();
        BigDecimal total = pedido.getDetalles().stream()
                .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoDashboardItem(
                pedido.getCodigoPedido(),
                cliente.isBlank() ? "—" : cliente,
                direccion,
                telefono,
                items,
                total,
                claveCss(pedido.getEstado()),
                pedido.getEstado().getEtiqueta(),
                hora,
                repartidor);
    }

    /** La clave que usa el CSS para colorear el estado. */
    private String claveCss(EstadoPedido estado) {
        return switch (estado) {
            case SOLICITADO -> "nuevo";
            case EN_PREPARACION -> "preparando";
            case LISTO_PARA_ENVIO -> "listo";
            case EN_CAMINO -> "encamino";
            case ENTREGADO -> "entregado";
            case CANCELADO -> "rechazado";
        };
    }

    private String primeraDireccion(Usuario usuario) {
        if (usuario == null || usuario.getDirecciones() == null || usuario.getDirecciones().isEmpty()) {
            return "—";
        }
        Direccion direccion = usuario.getDirecciones().get(0);
        return direccion.getDireccion() != null ? direccion.getDireccion() : "—";
    }

    private String primerTelefono(Usuario usuario) {
        if (usuario == null || usuario.getTelefonos() == null || usuario.getTelefonos().isEmpty()) {
            return "—";
        }
        Telefono telefono = usuario.getTelefonos().get(0);
        return telefono.getTelefono() != null ? telefono.getTelefono() : "—";
    }
}
