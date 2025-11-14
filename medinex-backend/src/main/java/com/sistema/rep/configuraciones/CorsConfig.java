package com.sistema.rep.configuraciones;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Permitir localhost (para desarrollo) y cualquier subdominio de Netlify (producción)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",
            "https://*.netlify.app"
        ));

        // 🔹 Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));

        // 🔹 Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 🔹 Headers expuestos al cliente (por ejemplo, para JWT)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // 🔹 Permitir cookies y credenciales
        configuration.setAllowCredentials(true);

        // 🔹 Cache de preflight (en segundos)
        configuration.setMaxAge(3600L);

        // 🔹 Registrar configuración para todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

