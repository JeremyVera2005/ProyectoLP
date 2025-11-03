package com.sistema.rep.controladores;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.rep.configuraciones.JwtUtils;
import com.sistema.rep.excepciones.UsuarioNotFoundException;
import com.sistema.rep.modelo.JwtRequest;
import com.sistema.rep.modelo.JwtResponse;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.servicios.UsuarioService;
import com.sistema.rep.servicios.impl.UserDetailsServiceImpl;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/generate-token")
    public ResponseEntity<?> generarToken(@RequestBody JwtRequest jwtRequest) {
        try {
            // Validar entrada
            if (jwtRequest == null || !StringUtils.hasText(jwtRequest.getUsername()) || 
                !StringUtils.hasText(jwtRequest.getPassword())) {
                logger.warn("Intento de autenticación con credenciales vacías");
                return ResponseEntity.badRequest().body("Usuario y contraseña son requeridos");
            }

            logger.info("Intentando autenticar usuario: {}", jwtRequest.getUsername());
            
            // Autenticar
            autenticar(jwtRequest.getUsername(), jwtRequest.getPassword());
            
            // Generar token
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
            String token = this.jwtUtils.generateToken(userDetails);
            
            logger.info("Token generado exitosamente para usuario: {}", jwtRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
            
        } catch (UsuarioNotFoundException exception) {
            logger.error("Usuario no encontrado: {}", jwtRequest.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } catch (Exception e) {
            logger.error("Error en autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación: " + e.getMessage());
        }
    }

    private void autenticar(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException exception) {
            logger.warn("Usuario deshabilitado: {}", username);
            throw new Exception("USUARIO DESHABILITADO " + exception.getMessage());
        } catch (BadCredentialsException e) {
            logger.warn("Credenciales inválidas para usuario: {}", username);
            throw new Exception("Credenciales inválidas " + e.getMessage());
        }
    }

    @GetMapping("/actual-usuario")
    public ResponseEntity<?> obtenerUsuarioActual(Principal principal) {
        try {
            // Validar si el principal existe
            if (principal == null || !StringUtils.hasText(principal.getName())) {
                logger.warn("Intento de obtener usuario actual sin autenticación");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }

            String username = principal.getName();
            logger.debug("Obteniendo usuario actual: {}", username);
            
            // Obtener el usuario directamente del servicio
            Usuario usuario = usuarioService.obtenerUsuario(username);
            
            if (usuario == null) {
                logger.warn("Usuario no encontrado en la base de datos: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Limpiar datos sensibles antes de enviar
            usuario.setPassword(null);
            
            return ResponseEntity.ok(usuario);
            
        } catch (Exception e) {
            logger.error("Error al obtener usuario actual: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}
