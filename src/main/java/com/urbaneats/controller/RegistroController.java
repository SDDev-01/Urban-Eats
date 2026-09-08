package com.urbaneats.controller;

import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Telefono;
import com.urbaneats.entity.Usuario;
import com.urbaneats.security.CustomUserDetailsService;
import com.urbaneats.service.IClienteService;
import com.urbaneats.service.IDireccionService;
import com.urbaneats.service.ITelefonoService;
import com.urbaneats.service.IUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
    private final IClienteService clienteService; // Inyecta el servicio de cliente si existe
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Repositorio necesario para persistir la sesión en Spring Security 6 / Spring Boot 3
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

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
                              HttpServletRequest request,
                              HttpServletResponse response,
                              RedirectAttributes redirect) {

        // Validaciones...
        if (usuarioService.existePorCorreo(Correo)) {
            redirect.addFlashAttribute("error", "Ya existe un usuario con ese correo");
            return "redirect:/registro";
        }

        try {
            // 1. Crear y encriptar Usuario
            Usuario usuario = new Usuario();
            usuario.setNombres(Nombres);
            usuario.setApellidos(Apellidos);
            usuario.setCorreo(Correo);
            // La contrasena se pasa en texto plano a proposito: UsuarioService.guardar
            // es quien la cifra con BCrypt. Si tambien se cifrara aqui quedaria guardada
            // como encode(encode(clave)) y ningun usuario podria volver a iniciar sesion.
            usuario.setPassword(Password);

            Usuario usuarioGuardado = usuarioService.guardar(usuario);

            // 2. Crear Teléfono
            Telefono telefono = new Telefono();
            telefono.setTelefono(Telefono);
            telefono.setUsuario(usuarioGuardado);
            telefonoService.guardar(telefono);

            // 3. Crear Dirección
            Direccion direccion = new Direccion();
            direccion.setDireccion(Direccion);
            direccion.setUsuario(usuarioGuardado);
            direccionService.guardar(direccion);

            // 4. El Cliente NO se crea aqui.
            // El trigger crear_cliente_automaticamente del Schema.sql inserta la fila
            // en cliente apenas se guarda el usuario. La tabla solo tiene CodigoCliente
            // y CodigoUsuario: los datos personales viven en usuario, telefono y direccion.

            // 5. Cargar UserDetails y autenticar
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuarioGuardado.getCorreo());
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 6. Guardar explícitamente en la sesión HTTP
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return "redirect:/catalogo";

        } catch (Exception e) {
            // Imprimir la traza completa en la consola para diagnosticar cualquier fallo SQL o JPA
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }
}