package tn.iteam.authservice.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return ResponseEntity.status(401).body(
            ApiError.of(401, "UNAUTHORIZED", "AUTH_INVALID_CREDENTIALS",
                "Email ou mot de passe incorrect", req.getRequestURI()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(DisabledException ex, HttpServletRequest req) {
        return ResponseEntity.status(401).body(
            ApiError.of(401, "UNAUTHORIZED", "AUTH_ACCOUNT_DISABLED",
                "Ce compte est désactivé. Contactez un administrateur.", req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(403).body(
            ApiError.of(403, "FORBIDDEN", "AUTH_INSUFFICIENT_ROLE",
                "Vous n'avez pas les droits nécessaires pour cette action.", req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        boolean emailExists = ex.getMessage() != null && ex.getMessage().contains("Email already exists");
        int status = emailExists ? 409 : 400;
        String httpStatus = emailExists ? "CONFLICT" : "BAD_REQUEST";
        String code = emailExists ? "AUTH_EMAIL_ALREADY_EXISTS" : "VALIDATION_ERROR";
        return ResponseEntity.status(status).body(
            ApiError.of(status, httpStatus, code, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(
            ApiError.of(400, "BAD_REQUEST", "VALIDATION_FAILED", details, req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500).body(
            ApiError.of(500, "INTERNAL_SERVER_ERROR", "SERVER_ERROR",
                "Une erreur interne est survenue.", req.getRequestURI()));
    }
}


