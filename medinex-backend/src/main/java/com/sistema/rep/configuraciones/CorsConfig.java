package com.sistema.rep.configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()               // 🔹 Habilita CORS
            .csrf().disable()           // 🔹 Desactiva CSRF para API REST
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 🔹 Permite todas las rutas y preflights
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Orígenes permitidos (localhost y Netlify)
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:4200",
            "https://*.netlify.app"
        ));

        // 🔹 Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of(
            "GET","POST","PUT","DELETE","OPTIONS","PATCH","HEAD"
        ));

        // 🔹 Headers permitidos
        configuration.setAllowedHeaders(List.of("*")); // Permite todos los headers

        // 🔹 Headers expuestos al cliente
        configuration.setExposedHeaders(List.of("Authorization","Content-Type"));

        // 🔹 Permitir cookies y credenciales
        configuration.setAllowCredentials(true);

        // 🔹 Tiempo de cache de preflight
        configuration.setMaxAge(3600L);

        // 🔹 Registrar configuración para todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
