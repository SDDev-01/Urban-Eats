package com.urbaneats.controller;

import com.urbaneats.dao.GerenteRepository;
import com.urbaneats.dao.RepartidorRepository;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Telefono;
import com.urbaneats.entity.Usuario;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.service.IDireccionService;
import com.urbaneats.service.ITelefonoService;
import com.urbaneats.service.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final IUsuarioService usuarioService;
    private final ITelefonoService telefonoService;
    private final IDireccionService direccionService;
    private final GerenteRepository gerenteRepository;
    private final RepartidorRepository repartidorRepository;

    public PerfilController(IUsuarioService usuarioService,
                            ITelefonoService telefonoService,
                            IDireccionService direccionService,
                            GerenteRepository gerenteRepository,
                            RepartidorRepository repartidorRepository) {
        this.usuarioService = usuarioService;
        this.telefonoService = telefonoService;
        this.direccionService = direccionService;
        this.gerenteRepository = gerenteRepository;
        this.repartidorRepository = repartidorRepository;
    }

    private CustomUserDetails getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping
    public String mostrarDatos(Model model) {
        CustomUserDetails usuarioAuth = getUsuarioActual();
        if (usuarioAuth == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorId(usuarioAuth.getCodigoUsuario())
                .orElse(null);

        if (usuario == null) {
            return "redirect:/login";
        }

        boolean esRepartidor = repartidorRepository
                .findByUsuario_CodigoUsuario(usuario.getCodigoUsuario())
                .isPresent();

        boolean tieneRestaurante = gerenteRepository
                .findByUsuario_CodigoUsuario(usuario.getCodigoUsuario())
                .isPresent();

        model.addAttribute("usuario", usuario);
        model.addAttribute("esRepartidor", esRepartidor);
        model.addAttribute("tieneRestaurante", tieneRestaurante);

        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            RedirectAttributes redirect) {

        CustomUserDetails usuarioAuth = getUsuarioActual();
        if (usuarioAuth == null) {
            return "redirect:/login";
        }

        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioAuth.getCodigoUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            usuario.setNombres(nombres);
            usuario.setApellidos(apellidos);
            usuario.setCorreo(correo);

            usuarioService.actualizar(usuario.getCodigoUsuario(), usuario);

            // Actualizar teléfono
            if (telefono != null && !telefono.trim().isEmpty()) {
                var telefonos = telefonoService.listarPorUsuario(usuario.getCodigoUsuario());
                if (telefonos.isEmpty()) {
                    Telefono t = new Telefono();
                    t.setTelefono(telefono);
                    t.setUsuario(usuario);
                    telefonoService.guardar(t);
                } else {
                    Telefono t = telefonos.get(0);
                    t.setTelefono(telefono);
                    telefonoService.actualizar(t.getCodigoTelefono(), t);
                }
            }

            // Actualizar dirección
            if (direccion != null && !direccion.trim().isEmpty()) {
                var direcciones = direccionService.listarPorUsuario(usuario.getCodigoUsuario());
                if (direcciones.isEmpty()) {
                    Direccion d = new Direccion();
                    d.setDireccion(direccion);
                    d.setUsuario(usuario);
                    direccionService.guardar(d);
                } else {
                    Direccion d = direcciones.get(0);
                    d.setDireccion(direccion);
                    direccionService.actualizar(d.getCodigoDireccion(), d);
                }
            }

            redirect.addFlashAttribute("exito", "Perfil actualizado correctamente");

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al actualizar el perfil");
        }

        return "redirect:/perfil";
    }
}