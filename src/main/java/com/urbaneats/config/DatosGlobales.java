package com.urbaneats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Datos que necesitan todas las vistas. Por ahora solo si el sistema esta en
 * modo demostracion, para mostrar la franja "Entorno de demostración".
 */
@ControllerAdvice
public class DatosGlobales {

    @Value("${urbaneats.modo-demo:false}")
    private boolean modoDemo;

    @ModelAttribute("modoDemo")
    public boolean modoDemo() {
        return modoDemo;
    }
}
