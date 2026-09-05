package com.urbaneats.controller;

import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Telefono;
import com.urbaneats.entity.Usuario;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.CustomUserDetailsService;
import com.urbaneats.service.IDireccionService;
import com.urbaneats.service.ITelefonoService;
import com.urbaneats.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class RegistroController {

    private final IUsuarioService usuarioService;
    private final ITelefonoService telefonoService;
    private final IDireccionService direccionService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/registro")
    public String mostrarPagina() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarse(@RequestParam String Nombres,
                              @RequestParam String Apellidos,
                              @RequestParam String Correo,
                              @RequestParam String Telefono,
                              @RequestParam String Password,
                              @RequestParam String Direccion,
                              RedirectAttributes redirect) {

        // Validacion de nombres y apellidos (solo letras, espacios, guiones, apostrofos, puntos)
        String nombreRegex = "^[\\p{L}\\s\\-'\\.]+$";
        if (!Pattern.matches(nombreRegex, Nombres)) {
            redirect.addFlashAttribute("error", "El campo Nombres contiene caracteres invalidos");
            return "redirect:/registro";
        }
        if (!Pattern.matches(nombreRegex, Apellidos)) {
            redirect.addFlashAttribute("error", "El campo Apellidos contiene caracteres invalidos");
            return "redirect:/registro";
        }

        // Validacion de correo
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Correo)) {
            redirect.addFlashAttribute("error", "El correo no tiene un formato valido");
            return "redirect:/registro";
        }

        // Validacion de password
        if (Password.length() < 8) {
            redirect.addFlashAttribute("error", "La contraseña debe tener al menos 8 caracteres");
            return "redirect:/registro";
        }

        // Validacion de telefono y direccion
        if (Telefono == null || Telefono.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "El telefono es obligatorio");
            return "redirect:/registro";
        }
        if (Direccion == null || Direccion.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "La direccion es obligatoria");
            return "redirect:/registro";
        }

        // Verificar que el correo no exista
        if (usuarioService.existePorCorreo(Correo)) {
            redirect.addFlashAttribute("error", "Ya existe un usuario con ese correo");
            return "redirect:/registro";
        }

        try {
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombres(Nombres);
            usuario.setApellidos(Apellidos);
            usuario.setCorreo(Correo);
            usuario.setPassword(Password);

            Usuario usuarioGuardado = usuarioService.guardar(usuario);

            // Crear telefono
            Telefono telefono = new Telefono();
            telefono.setTelefono(Telefono);
            telefono.setUsuario(usuarioGuardado);
            telefonoService.guardar(telefono);

            // Crear direccion
            Direccion direccion = new Direccion();
            direccion.setDireccion(Direccion);
            direccion.setUsuario(usuarioGuardado);
            direccionService.guardar(direccion);

            // Autenticar automaticamente (equivalente a session([...]) en Laravel)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuarioGuardado.getCorreo());
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            return "redirect:/catalogo";

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al registrar. Intenta nuevamente.");
            return "redirect:/registro";
        }
    }
}