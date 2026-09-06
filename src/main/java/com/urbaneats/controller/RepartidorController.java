package com.urbaneats.controller;

import com.urbaneats.entity.Envio;
import com.urbaneats.entity.Repartidor;
import com.urbaneats.service.IRepartidorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/repartidor")
public class RepartidorController {

    private final IRepartidorService repartidorService;

    public RepartidorController(IRepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    private String requiereLogin(HttpSession session) {
        Object codigoUsuario = session.getAttribute("CodigoUsuario");
        if (codigoUsuario == null) {
            return "redirect:/login";
        }
        return null;
    }

    @GetMapping
    public String mostrarFormulario(HttpSession session) {
        String redireccion = requiereLogin(session);
        if (redireccion != null) {
            return redireccion;
        }

        Integer codigoUsuario = (Integer) session.getAttribute("CodigoUsuario");
        Repartidor repartidor = repartidorService.buscarPorCodigoUsuario(codigoUsuario);

        if (repartidor != null) {
            return "redirect:/perfilRepartidor";
        }

        return "repartidor";
    }

    @PostMapping("/registrar")
    public String registrar(
            @RequestParam("TipoVehiculo") String tipoVehiculo,
            @RequestParam("Placa") String placa,
            @RequestParam(value = "SOAT", required = false) String soat,
            @RequestParam(value = "SeguroVehiculo", required = false) String seguroVehiculo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redireccion = requiereLogin(session);
        if (redireccion != null) {
            return redireccion;
        }

        Integer codigoUsuario = (Integer) session.getAttribute("CodigoUsuario");
        Repartidor existente = repartidorService.buscarPorCodigoUsuario(codigoUsuario);
        if (existente != null) {
            return "redirect:/perfilRepartidor";
        }

        try {
            repartidorService.registrarRepartidorConVehiculo(codigoUsuario, tipoVehiculo, placa, soat, seguroVehiculo);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/repartidor";
        }

        redirectAttributes.addFlashAttribute("exito", "¡Te has registrado como repartidor!");
        return "redirect:/perfilRepartidor";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        String redireccion = requiereLogin(session);
        if (redireccion != null) {
            return redireccion;
        }

        Integer codigoUsuario = (Integer) session.getAttribute("CodigoUsuario");
        Repartidor repartidor = repartidorService.obtenerPerfilConRelaciones(codigoUsuario);

        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        List<Envio> misEnvios = repartidorService.obtenerEnviosPorRepartidor(repartidor.getCodigoRepartidor());

        List<Envio> pedidosEnCurso = misEnvios.stream()
                .filter(e -> e.getPedido() != null && "En Proceso".equals(e.getPedido().getEstado()))
                .toList();

        List<Envio> pedidosRealizados = misEnvios.stream()
                .filter(e -> e.getPedido() != null && "Entregado".equals(e.getPedido().getEstado()))
                .toList();

        List<Envio> pedidosDisponibles = List.of();
        if (repartidor.getCodigoRepartidor() != 1) {
            pedidosDisponibles = repartidorService.obtenerPedidosDisponibles();
        }

        model.addAttribute("repartidor", repartidor);
        model.addAttribute("pedidosEnCurso", pedidosEnCurso);
        model.addAttribute("pedidosRealizados", pedidosRealizados);
        model.addAttribute("pedidosDisponibles", pedidosDisponibles);

        return "perfilRepartidor";
    }

    @PostMapping("/tomar-pedido/{envioId}")
    public String tomarPedido(@PathVariable("envioId") Integer envioId, HttpSession session, RedirectAttributes redirectAttributes) {
        String redireccion = requiereLogin(session);
        if (redireccion != null) {
            return redireccion;
        }

        Integer codigoUsuario = (Integer) session.getAttribute("CodigoUsuario");
        Repartidor repartidor = repartidorService.buscarPorCodigoUsuario(codigoUsuario);

        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        try {
            repartidorService.tomarPedido(envioId, repartidor.getCodigoRepartidor());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfilRepartidor";
        }

        redirectAttributes.addFlashAttribute("exito", "¡Pedido tomado! Ya aparece en tus envíos en camino.");
        return "redirect:/perfilRepartidor";
    }

    @PostMapping("/marcar-entregado/{envioId}")
    public String marcarEntregado(@PathVariable("envioId") Integer envioId, HttpSession session, RedirectAttributes redirectAttributes) {
        String redireccion = requiereLogin(session);
        if (redireccion != null) {
            return redireccion;
        }

        Integer codigoUsuario = (Integer) session.getAttribute("CodigoUsuario");
        Repartidor repartidor = repartidorService.buscarPorCodigoUsuario(codigoUsuario);

        if (repartidor == null) {
            return "redirect:/repartidor";
        }

        try {
            repartidorService.marcarEntregado(envioId, repartidor.getCodigoRepartidor());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfilRepartidor";
        }

        redirectAttributes.addFlashAttribute("exito", "¡Pedido marcado como entregado!");
        return "redirect:/perfilRepartidor";
    }
}