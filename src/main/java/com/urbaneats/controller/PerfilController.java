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
import com.urbaneats.service.ReglaNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

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
            @RequestParam(required = false) BigDecimal latitud,
            @RequestParam(required = false) BigDecimal longitud,
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
                    d.setLatitud(latitud);
                    d.setLongitud(longitud);
                    d.setUsuario(usuario);
                    direccionService.guardar(d);
                } else {
                    Direccion d = direcciones.get(0);
                    d.setDireccion(direccion);
                    if (latitud != null && longitud != null) {
                        d.setLatitud(latitud);
                        d.setLongitud(longitud);
                    }
                    direccionService.actualizar(d.getCodigoDireccion(), d);
                }
            }

            redirect.addFlashAttribute("exito", "Perfil actualizado correctamente");

        } catch (IllegalArgumentException | ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al actualizar el perfil");
        }

        return "redirect:/perfil";
    }

    /**
     * Pone el punto del mapa a una direccion (lo usa la pagina de pago cuando la
     * direccion elegida no tiene ubicacion). JSON: {"lat": 4.65, "lng": -74.06}.
     */
    @PostMapping("/direccion/{id}/ubicacion")
    @ResponseBody
    public ResponseEntity<?> guardarUbicacion(@PathVariable Integer id, @RequestBody Map<String, Object> cuerpo) {
        CustomUserDetails usuario = getUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Inicia sesión de nuevo."));
        }
        try {
            BigDecimal lat = new BigDecimal(String.valueOf(cuerpo.get("lat")));
            BigDecimal lng = new BigDecimal(String.valueOf(cuerpo.get("lng")));
            Direccion direccion = direccionService.actualizarUbicacion(id, usuario.getCodigoUsuario(), lat, lng);
            return ResponseEntity.ok(Map.of("codigo", direccion.getCodigoDireccion(),
                    "lat", direccion.getLatitud(), "lng", direccion.getLongitud()));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Coordenadas inválidas."));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        }
    }
}
