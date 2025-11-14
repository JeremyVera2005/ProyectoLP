package com.sistema.rep.configuraciones;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CorsConfig {

    // 🔹 Configuración de seguridad + CORS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors() // habilita CORS
            .and()
            .csrf().disable() // desactivar CSRF si es API
            .authorizeHttpRequests()
            .anyRequest().permitAll(); // permitir todas las rutas (ajustar según seguridad real)
        return http.build();
    }

    // 🔹 Configuración de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Orígenes permitidos
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",  // Angular local
                "https://leafy-fudge-2633b7.netlify.app" // Netlify
        ));

        // 🔹 Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));

        // 🔹 Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 🔹 Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type"
        ));

        // 🔹 Permitir credenciales
        configuration.setAllowCredentials(true);

        // 🔹 Tiempo de cache para preflight requests
        configuration.setMaxAge(3600L);

        // 🔹 Registrar la configuración para todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}



