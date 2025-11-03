package com.sistema.rep.controladores;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.rep.modelo.Rol;
import com.sistema.rep.modelo.Usuario;
import com.sistema.rep.modelo.UsuarioRol;
import com.sistema.rep.servicios.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/")
    public ResponseEntity<?> guardarUsuario(@RequestBody Usuario usuario) {
        try {
            // Validaciones básicas
            if (usuario == null || !StringUtils.hasText(usuario.getUsername()) || !StringUtils.hasText(usuario.getPassword())) {
                return ResponseEntity.badRequest().body("Usuario, nombre de usuario y contraseña son requeridos");
            }
            
            // Verificar si el usuario ya existe
            if (usuarioService.obtenerUsuario(usuario.getUsername()) != null) {
                return ResponseEntity.badRequest().body("El usuario ya existe");
            }
            
            usuario.setPerfil("default.png");
            usuario.setPassword(this.bCryptPasswordEncoder.encode(usuario.getPassword()));

            Set<UsuarioRol> usuarioRoles = new HashSet<>();

            Rol rol = new Rol();
            rol.setRolId(2L);
            rol.setRolNombre("NORMAL");

            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuario(usuario);
            usuarioRol.setRol(rol);

            usuarioRoles.add(usuarioRol);
            Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario, usuarioRoles);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el usuario: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable("username") String username) {
        try {
            if (!StringUtils.hasText(username)) {
                return ResponseEntity.badRequest().body("El nombre de usuario es requerido");
            }
            Usuario usuario = usuarioService.obtenerUsuario(username);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario: " + e.getMessage());
        }
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable("usuarioId") Long usuarioId) {
        try {
            if (usuarioId == null || usuarioId <= 0) {
                return ResponseEntity.badRequest().body("ID de usuario inválido");
            }
            usuarioService.eliminarUsuario(usuarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario: " + e.getMessage());
        }
    }
}
