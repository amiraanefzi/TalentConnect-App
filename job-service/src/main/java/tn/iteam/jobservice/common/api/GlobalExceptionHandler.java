package tn.iteam.jobservice.common.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.iteam.jobservice.common.errors.NotFoundException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var violations = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toViolation)
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), violations);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String name = ex.getName();
        Object value = ex.getValue();
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "required type";
        String msg = "Invalid value for parameter '" + name + "': '" + value + "'. Expected " + expected + ".";
        return error(HttpStatus.BAD_REQUEST, msg, request.getRequestURI(), List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Malformed JSON request", request.getRequestURI(), List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI(), List.of());
    }

    private ApiError.FieldViolation toViolation(FieldError fe) {
        return new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, String path, List<ApiError.FieldViolation> violations) {
        var body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                violations
        );
        return ResponseEntity.status(status).body(body);
    }
}
