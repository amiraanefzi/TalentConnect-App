package com.talentconnect.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(ResourceNotFoundException ex){ return error(ex.getMessage(),HttpStatus.NOT_FOUND); }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicate(DuplicateResourceException ex){ return error(ex.getMessage(),HttpStatus.CONFLICT); }
    @ExceptionHandler({ForbiddenException.class,AccessDeniedException.class})
    public ResponseEntity<Map<String,Object>> handleForbidden(Exception ex){ return error(ex.getMessage(),HttpStatus.FORBIDDEN); }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String,Object>> handleUnauth(AuthenticationException ex){ return error(ex.getMessage(),HttpStatus.UNAUTHORIZED); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){ return error(ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", ")),HttpStatus.BAD_REQUEST); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex){ return error("Erreur interne: "+ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR); }
    private ResponseEntity<Map<String,Object>> error(String message,HttpStatus status){ return ResponseEntity.status(status).body(Map.of("error",message,"status",status.value(),"timestamp",Instant.now().toString())); }
}
