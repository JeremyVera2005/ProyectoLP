package com.sistema.rep.configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    // 🔹 Seguridad + CORS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors() // habilita CORS
            .and()
            .csrf().disable() // desactivar CSRF para API
            .authorizeHttpRequests()
            .anyRequest().permitAll(); // permitir todas las rutas

        return http.build();
    }

    // 🔹 Configuración de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Permitir localhost y cualquier deploy de Netlify
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200",
                "https://*.netlify.app"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET","POST","PUT","DELETE","OPTIONS","PATCH","HEAD"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
