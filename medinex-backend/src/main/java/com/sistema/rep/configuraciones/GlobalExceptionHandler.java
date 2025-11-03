package com.sistema.rep.configuraciones;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.sistema.rep.excepciones.UsuarioFoundException;
import com.sistema.rep.excepciones.UsuarioNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsuarioFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioFoundException(UsuarioFoundException ex, WebRequest request) {
        logger.warn("Usuario ya existe: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Usuario ya existe");
        response.put("message", ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNotFoundException(UsuarioNotFoundException ex, WebRequest request) {
        logger.warn("Usuario no encontrado: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Usuario no encontrado");
        response.put("message", ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Error interno del servidor", ex);
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error interno del servidor");
        response.put("message", "Ha ocurrido un error inesperado");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Argumento inválido: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Argumento inválido");
        response.put("message", ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
