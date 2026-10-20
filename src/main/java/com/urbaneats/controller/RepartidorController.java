package com.urbaneats.controller;

import com.urbaneats.dao.EnvioRepository;
import com.urbaneats.dao.RepartidorRepository;
import com.urbaneats.dao.RolRepository;
import com.urbaneats.dao.UsuarioRepository;
import com.urbaneats.dao.VehiculoRepository;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.EstadoPedido;
import com.urbaneats.entity.Repartidor;
import com.urbaneats.entity.Rol;
import com.urbaneats.entity.Usuario;
import com.urbaneats.entity.Vehiculo;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;
import com.urbaneats.service.PedidoEstadoService;
import com.urbaneats.service.ReglaNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador de repartidor: registro, panel y flujo de toma/entrega de pedidos.
 *
 * Reglas (las hace cumplir PedidoEstadoService):
 *   - Solo se ven y se toman pedidos "Listo para envio" que nadie haya tomado.
 *   - Tomar es atomico: dos repartidores no pueden quedarse con el mismo pedido.
 *   - "Liberar" devuelve el pedido a la lista (antes el boton cancelaba el pedido del cliente).
 *   - Solo el repartidor asignado puede entregar, y solo si el pedido va "En camino".
 */
@Controller
public class RepartidorController {

    private static final Set<String> TIPOS_VEHICULO_VALIDOS = Set.of("Moto", "Carro", "Bicicleta");
    private static final String NOMBRE_ROL_REPARTIDOR = "Repartidor";

    private final RepartidorRepository repartidorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EnvioRepository envioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PedidoEstadoService pedidoEstadoService;
    private final SesionHelper sesionHelper;

    public RepartidorController(RepartidorRepository repartidorRepository,
                                VehiculoRepository vehiculoRepository,
                                EnvioRepository envioRepository,
                                UsuarioRepository usuarioRepository,
                                RolRepository rolRepository,
                                PedidoEstadoService pedidoEstadoService,
                                SesionHelper sesionHelper) {
        this.repartidorRepository = repartidorRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.envioRepository = envioRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.pedidoEstadoService = pedidoEstadoService;
        this.sesionHelper = sesionHelper;
    }

    /** El repartidor del usuario que inicio sesion, o null si todavia no se ha registrado como tal. */
    private Repartidor repartidorActual() {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        return repartidorRepository.findByUsuario_CodigoUsuario(usuario.getCodigoUsuario()).orElse(null);
    }

    @GetMapping("/repartidor")
    public String mostrarFormulario(Model model) {
        if (repartidorActual() != null) {
            return "redirect:/perfilRepartidor";
        }
        model.addAttribute("nombreUsuario", SesionHelper.usuarioActual().getNombres());
        return "repartidor";
    }

    /**
     * Registro como repartidor. La transaccion evita que un doble envio del formulario
     * deje dos filas para el mismo usuario. Ademas ahora se le da el rol Repartidor
     * (antes no se le asignaba) y se recarga la sesion para que pueda entrar a su panel.
     */
    @PostMapping("/repartidor")
    @Transactional
    public String registrar(@RequestParam String tipoVehiculo,
                            @RequestParam String placa,
                            @RequestParam(required = false) String soat,
                            @RequestParam(required = false) String seguroVehiculo,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            RedirectAttributes redirect) {
        CustomUserDetails usuarioActual = SesionHelper.usuarioActual();

        if (repartidorActual() != null) {
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
        String placaNormalizada = placa.trim().toUpperCase().replace(" ", "").replace("-", "");
        if (!"Bicicleta".equals(tipoVehiculo) && !placaNormalizada.matches("[A-Z]{3}[0-9]{2}[A-Z0-9]")) {
            redirect.addFlashAttribute("error", "La placa debe tener el formato colombiano: ABC123 (carro) o ABC12D (moto).");
            return "redirect:/repartidor";
        }
        if (vehiculoRepository.existsById(placaNormalizada)) {
            redirect.addFlashAttribute("error", "Esa placa ya está registrada.");
            return "redirect:/repartidor";
        }

        Usuario usuario = usuarioRepository.findById(usuarioActual.getCodigoUsuario()).orElseThrow();

        Repartidor repartidor = new Repartidor();
        repartidor.setUsuario(usuario);
        repartidor = repartidorRepository.save(repartidor);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placaNormalizada);
        vehiculo.setRepartidor(repartidor);
        vehiculo.setTipoVehiculo(tipoVehiculo);
        vehiculo.setSoat(soat);
        vehiculo.setSeguroVehiculo(seguroVehiculo);
        vehiculoRepository.save(vehiculo);

        if (!usuario.tieneRol(NOMBRE_ROL_REPARTIDOR)) {
            Rol rol = rolRepository.findByNombreRol(NOMBRE_ROL_REPARTIDOR).orElseThrow();
            usuario.getRoles().add(rol);
            usuarioRepository.saveAndFlush(usuario);
        }
        sesionHelper.recargarRoles(request, response);

        redirect.addFlashAttribute("exito", "¡Te has registrado como repartidor!");
        return "redirect:/perfilRepartidor";
    }

    @GetMapping("/perfilRepartidor")
    public String mostrarPerfil(Model model) {
        Repartidor repartidor = repartidorActual();
        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        List<Envio> pedidosEnCurso = envioRepository
                .findByRepartidor_CodigoRepartidorAndPedido_EstadoOrderByFechaEnvioDesc(
                        repartidor.getCodigoRepartidor(), EstadoPedido.EN_CAMINO);
        List<Envio> pedidosRealizados = envioRepository
                .findByRepartidor_CodigoRepartidorAndPedido_EstadoOrderByFechaEnvioDesc(
                        repartidor.getCodigoRepartidor(), EstadoPedido.ENTREGADO);
        List<Envio> pedidosDisponibles = envioRepository
                .findByRepartidorIsNullAndPedido_EstadoOrderByCodigoEnvioAsc(EstadoPedido.LISTO_PARA_ENVIO);

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
        return ejecutar(redirect, "¡Pedido tomado! Recógelo en la cocina y llévalo al cliente.",
                repartidor -> pedidoEstadoService.tomar(envioId, repartidor, SesionHelper.usuarioActual().getCodigoUsuario()));
    }

    @PostMapping("/repartidor/liberar/{envioId}")
    public String liberarPedido(@PathVariable Integer envioId, RedirectAttributes redirect) {
        return ejecutar(redirect, "Liberaste el pedido: vuelve a la lista para otro repartidor.",
                repartidor -> pedidoEstadoService.liberar(envioId, repartidor, SesionHelper.usuarioActual().getCodigoUsuario()));
    }

    @PostMapping("/repartidor/entregar/{envioId}")
    public String marcarEntregado(@PathVariable Integer envioId, RedirectAttributes redirect) {
        return ejecutar(redirect, "¡Pedido marcado como entregado!",
                repartidor -> pedidoEstadoService.entregar(envioId, repartidor, SesionHelper.usuarioActual().getCodigoUsuario()));
    }

    /**
     * Recibe la posicion del repartidor (cada pocos segundos) desde su navegador:
     * el GPS del celular o el boton "Simular recorrido" de la demostracion.
     * Cuerpo: {"envioId": 12, "lat": 4.65, "lng": -74.06}
     */
    @PostMapping("/repartidor/ubicacion")
    @ResponseBody
    public ResponseEntity<?> actualizarUbicacion(@RequestBody Map<String, Object> cuerpo) {
        Repartidor repartidor = repartidorActual();
        if (repartidor == null) {
            return ResponseEntity.status(403).body(Map.of("error", "No estás registrado como repartidor."));
        }
        try {
            Integer envioId = Integer.valueOf(String.valueOf(cuerpo.get("envioId")));
            BigDecimal lat = new BigDecimal(String.valueOf(cuerpo.get("lat")));
            BigDecimal lng = new BigDecimal(String.valueOf(cuerpo.get("lng")));
            pedidoEstadoService.actualizarUbicacion(envioId, repartidor, lat, lng);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos de ubicación inválidos."));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    /** Ejecuta una accion del repartidor y traduce el resultado en un mensaje para la pantalla. */
    private String ejecutar(RedirectAttributes redirect, String mensajeExito,
                            java.util.function.Consumer<Repartidor> accion) {
        Repartidor repartidor = repartidorActual();
        if (repartidor == null) {
            return "redirect:/repartidor";
        }
        try {
            accion.accept(repartidor);
            redirect.addFlashAttribute("exito", mensajeExito);
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/perfilRepartidor";
    }
}
