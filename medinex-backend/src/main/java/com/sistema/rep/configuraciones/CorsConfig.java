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
        
        // **LISTA DE ORÍGENES PERMITIDOS (KEY FIX)**
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200", // Para desarrollo local de Angular/Frontend
            "https://*.netlify.app",    // Para subdominios simples (ej. mi-sitio.netlify.app)
            "https://*.*.netlify.app",  // Para subdominios anidados (ej. previews de Netlify)
            // TU PERMALINK ESPECÍFICO (El que estaba siendo bloqueado)
            "https://6917869dedf05d1b491391dd--relaxed-sunshine-f672cd.netlify.app" 
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // Cabeceras permitidas (el '*' permite todas)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Cabeceras que el navegador debe exponer al frontend (útil para JWT)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Permitir el envío de cookies y credenciales de autenticación
        configuration.setAllowCredentials(true);
        
        // Tiempo máximo en segundos para almacenar en caché la respuesta preflight (OPTIONS)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración CORS a todas las rutas ("/**") de tu API
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
