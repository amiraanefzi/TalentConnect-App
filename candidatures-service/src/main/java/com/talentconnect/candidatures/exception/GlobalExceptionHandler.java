package com.talentconnect.candidatures.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.talentconnect.candidatures.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
	}

	@ExceptionHandler({ ForbiddenException.class, AccessDeniedException.class })
	ResponseEntity<ErrorResponse> handleForbidden(RuntimeException exception, HttpServletRequest request) {
		return error(HttpStatus.FORBIDDEN, exception.getMessage(), request, List.of());
	}

	@ExceptionHandler(DuplicateCandidatureException.class)
	ResponseEntity<ErrorResponse> handleDuplicate(DuplicateCandidatureException exception, HttpServletRequest request) {
		return error(HttpStatus.CONFLICT, exception.getMessage(), request, List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		List<String> details = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();
		return error(HttpStatus.BAD_REQUEST, "Requete invalide", request, details);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception,
			HttpServletRequest request) {
		log.warn("Database constraint violation", exception);
		return error(HttpStatus.CONFLICT, "La candidature existe deja ou viole une contrainte", request, List.of());
	}

	@ExceptionHandler(InvalidFileException.class)
	ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException exception, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request, List.of());
	}

	@ExceptionHandler(FileServiceIntegrationException.class)
	ResponseEntity<ErrorResponse> handleFileServiceIntegration(FileServiceIntegrationException exception,
			HttpServletRequest request) {
		log.warn("File service integration error", exception);
		return error(HttpStatus.BAD_GATEWAY, "Erreur d'integration avec file-service", request, List.of());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
		log.error("Unexpected error", exception);
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du service candidatures", request, List.of());
	}

	private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, HttpServletRequest request,
			List<String> details) {
		return ResponseEntity.status(status).body(new ErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				details));
	}
}
