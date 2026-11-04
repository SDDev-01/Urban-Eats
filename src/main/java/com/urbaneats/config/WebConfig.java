package com.urbaneats.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra RolActivoInterceptor para todas las paginas (no para los archivos estaticos). */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RolActivoInterceptor rolActivoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rolActivoInterceptor)
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**", "/favicon.ico", "/error");
    }
}
