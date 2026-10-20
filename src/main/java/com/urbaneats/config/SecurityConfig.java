package com.urbaneats.config;

import com.urbaneats.security.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Quien puede entrar a cada pagina.
 *
 * Los roles salen de la tabla rol_usuario (CustomUserDetailsService les antepone "ROLE_"):
 *   CLIENTE, GERENTE (cocina), REPARTIDOR y TECNICO (verifica cocinas y ve reportes).
 * Antes solo se distinguia "con sesion" o "sin sesion": cualquier usuario podia abrir
 * el panel de una cocina o tomar pedidos como repartidor.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Paginas publicas y archivos estaticos
                .requestMatchers(
                    "/", "/login", "/registro",
                    "/catalogo", "/restaurantes", "/restauranteDetalle",
                    "/chatbot", "/carrito", "/error",
                    "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**", "/favicon.ico"
                ).permitAll()
                // Cocina (gerente)
                .requestMatchers("/perfilRestaurante/**", "/menu/**", "/seleccion-restaurante/**").hasRole("GERENTE")
                // Repartidor (el registro /repartidor queda para cualquier usuario con sesion)
                .requestMatchers("/perfilRepartidor/**", "/repartidor/tomar/**", "/repartidor/liberar/**",
                                 "/repartidor/entregar/**", "/repartidor/ubicacion").hasRole("REPARTIDOR")
                // Tecnico de Urban Eats: verificacion de cocinas
                .requestMatchers("/tecnico/**").hasRole("TECNICO")
                // Reportes: la cocina ve los suyos, el tecnico ve todos
                .requestMatchers("/reportes/**").hasAnyRole("GERENTE", "TECNICO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
