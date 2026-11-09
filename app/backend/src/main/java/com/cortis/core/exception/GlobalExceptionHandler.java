package com.cortis.core.exception;

import com.cortis.core.exception.ex.LanguageNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                          HttpServletRequest request) {
        List<ValidationError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> new ValidationError(field.getField(), field.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "common.error.validation_error",
                request, errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException exception,
                                                              HttpServletRequest request) {
        // Same generic message for invalid username, password, chip or disabled account.
        return build(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", "auth.error.invalid_credentials",
                request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleDenied(AccessDeniedException exception,
                                                      HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "common.error.access_denied", request, null);
    }

    @ExceptionHandler(LanguageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLanguage(LanguageNotFoundException exception,
                                                        HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "TRANSLATION_LANGUAGE_NOT_FOUND",
                "translation.error.language_not_found", request, null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException exception,
                                                      HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = status == HttpStatus.NOT_FOUND ? "RESOURCE_NOT_FOUND" : "REQUEST_REJECTED";
        return build(status, code, "common.error." + code.toLowerCase(java.util.Locale.ROOT), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled backend exception at {} {}", request.getMethod(), request.getRequestURI(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "common.error.internal_server", request, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String messageKey,
                                                HttpServletRequest request, List<ValidationError> validationErrors) {
        ErrorResponse response = new ErrorResponse(Instant.now(), status.value(), code,
                messageKey, null, request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).header("Cache-Control", "no-store").body(response);
    }
}
