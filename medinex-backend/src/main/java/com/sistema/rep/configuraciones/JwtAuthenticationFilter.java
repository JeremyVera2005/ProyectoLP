package com.sistema.rep.configuraciones;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sistema.rep.servicios.impl.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // Extraer el token JWT del header Authorization
        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            jwtToken = requestTokenHeader.substring(7);

            try{
                username = this.jwtUtil.extractUsername(jwtToken);
            }catch (ExpiredJwtException exception){
                log.warn("El token ha expirado para la solicitud: {}", request.getRequestURI());
            }catch (Exception e){
                log.error("Error al procesar el token JWT: {}", e.getMessage());
            }

        }else{
            log.debug("Token JWT no encontrado o no tiene formato Bearer para: {}", request.getRequestURI());
        }

        // Validar y establecer la autenticación
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if(this.jwtUtil.validateToken(jwtToken, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("Usuario autenticado exitosamente: {}", username);
                } else {
                    log.debug("Token JWT inválido para el usuario: {}", username);
                }
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                log.warn("Usuario no encontrado: {}", username);
            } catch (Exception e) {
                log.error("Error al validar el usuario: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
