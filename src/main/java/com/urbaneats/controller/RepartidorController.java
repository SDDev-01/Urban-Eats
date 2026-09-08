package com.urbaneats.controller;

import com.urbaneats.dao.EnvioRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.dao.RepartidorRepository;
import com.urbaneats.dao.UsuarioRepository;
import com.urbaneats.dao.VehiculoRepository;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Repartidor;
import com.urbaneats.entity.Vehiculo;
import com.urbaneats.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Controlador de repartidor: registro, perfil y flujo de toma/entrega de pedidos.
 * Equivale a RepartidorController de la rama php-Laravel.
 * La autenticacion la resuelve Spring Security (SecurityConfig exige login para estas rutas),
 * por eso no hay un requiereLogin() explicito como en PHP.
 */
@Controller
public class RepartidorController {

    private static final Set<String> TIPOS_VEHICULO_VALIDOS = Set.of("Moto", "Carro", "Bicicleta", "Bici");

    /** Repartidor por defecto asignado a todo envio nuevo (ver PagoService). Se excluye de "disponibles"
     *  para que no vea duplicados los pedidos que el sistema ya le puso por defecto. */
    private static final Integer CODIGO_REPARTIDOR_DEFECTO = 1;

    private final RepartidorRepository repartidorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EnvioRepository envioRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public RepartidorController(RepartidorRepository repartidorRepository,
                                 VehiculoRepository vehiculoRepository,
                                 EnvioRepository envioRepository,
                                 PedidoRepository pedidoRepository,
                                 UsuarioRepository usuarioRepository) {
        this.repartidorRepository = repartidorRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private CustomUserDetails getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/repartidor")
    public String mostrarFormulario(Model model) {
        CustomUserDetails usuarioActual = getUsuarioActual();

        boolean yaEsRepartidor = repartidorRepository
                .findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .isPresent();
        if (yaEsRepartidor) {
            return "redirect:/perfilRepartidor";
        }

        model.addAttribute("nombreUsuario", usuarioActual.getNombres());
        return "repartidor";
    }

    @PostMapping("/repartidor")
    public String registrar(@RequestParam String tipoVehiculo,
                             @RequestParam String placa,
                             @RequestParam(required = false) String soat,
                             @RequestParam(required = false) String seguroVehiculo,
                             RedirectAttributes redirect) {
        CustomUserDetails usuarioActual = getUsuarioActual();

        if (repartidorRepository.findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario()).isPresent()) {
            return "redirect:/perfilRepartidor";
        }

        if (tipoVehiculo == null || !TIPOS_VEHICULO_VALIDOS.contains(tipoVehiculo)) {
            redirect.addFlashAttribute("error", "Selecciona un tipo de vehículo válido.");
            return "redirect:/repartidor";
        }
        if (placa == null || placa.isBlank()) {
            redirect.addFlashAttribute("error", "La placa es obligatoria.");
            return "redirect:/repartidor";
        }
        String placaNormalizada = placa.trim().toUpperCase();
        if (vehiculoRepository.existsById(placaNormalizada)) {
            redirect.addFlashAttribute("error", "Esa placa ya está registrada.");
            return "redirect:/repartidor";
        }

        Repartidor repartidor = new Repartidor();
        repartidor.setUsuario(usuarioRepository.getReferenceById(usuarioActual.getCodigoUsuario()));
        repartidor = repartidorRepository.save(repartidor);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placaNormalizada);
        vehiculo.setRepartidor(repartidor);
        vehiculo.setTipoVehiculo(tipoVehiculo);
        vehiculo.setSoat(soat);
        vehiculo.setSeguroVehiculo(seguroVehiculo);
        vehiculoRepository.save(vehiculo);

        redirect.addFlashAttribute("exito", "¡Te has registrado como repartidor!");
        return "redirect:/perfilRepartidor";
    }

    @GetMapping("/perfilRepartidor")
    public String mostrarPerfil(Model model) {
        CustomUserDetails usuarioActual = getUsuarioActual();

        Repartidor repartidor = repartidorRepository
                .findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .orElse(null);
        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        List<Envio> pedidosEnCurso = envioRepository
                .findByRepartidor_CodigoRepartidorAndPedido_EstadoOrderByFechaEnvioDesc(
                        repartidor.getCodigoRepartidor(), "En Proceso");
        List<Envio> pedidosRealizados = envioRepository
                .findByRepartidor_CodigoRepartidorAndPedido_EstadoOrderByFechaEnvioDesc(
                        repartidor.getCodigoRepartidor(), "Entregado");

        List<Envio> pedidosDisponibles = repartidor.getCodigoRepartidor().equals(CODIGO_REPARTIDOR_DEFECTO)
                ? List.of()
                : envioRepository.findByPedido_EstadoOrderByFechaEnvioDesc("Iniciando");

        model.addAttribute("repartidor", repartidor);
        model.addAttribute("pedidosEnCurso", conTotales(pedidosEnCurso));
        model.addAttribute("pedidosDisponibles", conTotales(pedidosDisponibles));
        model.addAttribute("pedidosRealizados", conTotales(pedidosRealizados));
        return "perfilRepartidor";
    }

    private List<EnvioConTotal> conTotales(List<Envio> envios) {
        return envios.stream()
                .map(envio -> new EnvioConTotal(envio, totalDelPedido(envio)))
                .toList();
    }

    private BigDecimal totalDelPedido(Envio envio) {
        if (envio.getPedido() == null || envio.getPedido().getDetalles() == null) {
            return BigDecimal.ZERO;
        }
        return envio.getPedido().getDetalles().stream()
                .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PostMapping("/repartidor/tomar/{envioId}")
    public String tomarPedido(@PathVariable Integer envioId, RedirectAttributes redirect) {
        CustomUserDetails usuarioActual = getUsuarioActual();

        Repartidor repartidor = repartidorRepository
                .findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .orElse(null);
        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        Envio envio = envioRepository.findById(envioId).orElse(null);
        if (envio == null || envio.getPedido() == null || !"Iniciando".equals(envio.getPedido().getEstado())) {
            redirect.addFlashAttribute("error", "Ese pedido ya no está disponible.");
            return "redirect:/perfilRepartidor";
        }

        envio.setRepartidor(repartidor);
        envioRepository.save(envio);

        Pedido pedido = envio.getPedido();
        pedido.setEstado("En Proceso");
        pedidoRepository.save(pedido);

        redirect.addFlashAttribute("exito", "¡Pedido tomado! Ya aparece en tus envíos en camino.");
        return "redirect:/perfilRepartidor";
    }

    @PostMapping("/repartidor/entregar/{envioId}")
    public String marcarEntregado(@PathVariable Integer envioId, RedirectAttributes redirect) {
        CustomUserDetails usuarioActual = getUsuarioActual();

        Repartidor repartidor = repartidorRepository
                .findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .orElse(null);
        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        Envio envio = envioRepository.findById(envioId).orElse(null);
        boolean perteneceAlRepartidor = envio != null
                && envio.getRepartidor() != null
                && repartidor.getCodigoRepartidor().equals(envio.getRepartidor().getCodigoRepartidor());
        if (!perteneceAlRepartidor || envio.getPedido() == null) {
            redirect.addFlashAttribute("error", "Ese envío no te pertenece.");
            return "redirect:/perfilRepartidor";
        }

        Pedido pedido = envio.getPedido();
        pedido.setEstado("Entregado");
        pedidoRepository.save(pedido);

        redirect.addFlashAttribute("exito", "¡Pedido marcado como entregado!");
        return "redirect:/perfilRepartidor";
    }
}
