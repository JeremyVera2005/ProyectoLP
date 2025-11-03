package com.sistema.rep.utils;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * Clase utilitaria para validaciones comunes
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9]{10,15}$");

    /**
     * Valida si una cadena no está vacía
     */
    public static boolean isValidString(String str) {
        return StringUtils.hasText(str) && str.trim().length() > 0;
    }

    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(String email) {
        return isValidString(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida formato de teléfono
     */
    public static boolean isValidPhone(String phone) {
        return isValidString(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Valida que la contraseña tenga una longitud mínima
     */
    public static boolean isValidPassword(String password) {
        return isValidString(password) && password.length() >= 6;
    }

    /**
     * Valida que un ID sea válido (mayor que 0)
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * Valida que una cadena tenga una longitud específica
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        return isValidString(str) && str.length() >= minLength && str.length() <= maxLength;
    }
}
