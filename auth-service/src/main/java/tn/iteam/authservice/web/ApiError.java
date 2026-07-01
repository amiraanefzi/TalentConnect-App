package tn.iteam.authservice.web;

import java.time.Instant;

/**
 * Format standard d'erreur JSON pour TOUS les services TalentConnect.
 *
 * Exemple JSON :
 * {
 *   "timestamp": "2026-06-29T10:30:00Z",
 *   "status": 401,
 *   "error": "UNAUTHORIZED",
 *   "code": "AUTH_INVALID_CREDENTIALS",
 *   "message": "Email ou mot de passe incorrect",
 *   "path": "/api/auth/login"
 * }
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path
) {
    public static ApiError of(int status, String error, String code, String message, String path) {
        return new ApiError(Instant.now(), status, error, code, message, path);
    }
}

