package com.urbaneats.controller;

import com.urbaneats.entity.Usuario;
import com.urbaneats.service.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final IUsuarioService usuarioService;

    public PerfilController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarDatos(Model model) {
    
        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion) {
        
    
        return "redirect:/perfil";
    }
}