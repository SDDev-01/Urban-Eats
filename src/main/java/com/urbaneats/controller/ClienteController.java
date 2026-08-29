package com.urbaneats.controller;

import com.urbaneats.domain.Cliente;
import com.urbaneats.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/listado")
    public String inicio(Model model) {
        var clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        return "cliente/listado"; // Apunta a una vista HTML que tus compañeros o tú diseñen
    }

    @GetMapping("/nuevo")
    public String clienteNuevo(Cliente cliente) {
        return "cliente/modifica";
    }

    @PostMapping("/guardar")
    public String clienteGuardar(Cliente cliente) {
        clienteService.guardarCliente(cliente);
        return "redirect:/cliente/listado";
    }

    @GetMapping("/eliminar/{idCliente}")
    public String clienteEliminar(Cliente cliente) {
        clienteService.eliminarCliente(cliente.getIdCliente());
        return "redirect:/cliente/listado";
    }

    @GetMapping("/modificar/{idCliente}")
    public String clienteModificar(Cliente cliente, Model model) {
        cliente = clienteService.obtenerClientePorId(cliente.getIdCliente()).orElse(null);
        model.addAttribute("cliente", cliente);
        return "cliente/modifica";
    }
}