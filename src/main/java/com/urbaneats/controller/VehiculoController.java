package com.urbaneats.controller;

import com.urbaneats.domain.Vehiculo;
import com.urbaneats.service.RepartidorService;
import com.urbaneats.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private RepartidorService repartidorService;

    @GetMapping("/listado")
    public String listarVehiculos(Model model) {
        var vehiculos = vehiculoService.listarVehiculos();
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("totalVehiculos", vehiculos.size());
        return "vehiculo/listado";
    }

    @GetMapping("/nuevo")
    public String vehiculoNuevo(Vehiculo vehiculo, Model model) {
        model.addAttribute("repartidores", repartidorService.listarRepartidores());
        return "vehiculo/modifica";
    }

    @PostMapping("/guardar")
    public String vehiculoGuardar(Vehiculo vehiculo) {
        vehiculoService.guardarVehiculo(vehiculo);
        return "redirect:/vehiculo/listado";
    }

    @GetMapping("/eliminar/{idVehiculo}")
    public String vehiculoEliminar(Vehiculo vehiculo) {
        vehiculoService.eliminarVehiculo(vehiculo.getIdVehiculo());
        return "redirect:/vehiculo/listado";
    }

    @GetMapping("/modificar/{idVehiculo}")
    public String vehiculoModificar(Vehiculo vehiculo, Model model) {
        vehiculo = vehiculoService.obtenerVehiculoPorId(vehiculo.getIdVehiculo()).orElse(null);
        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("repartidores", repartidorService.listarRepartidores());
        return "vehiculo/modifica";
    }
}