package com.cortis.desktop.core.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String messageKey,
        String message,
        String path,
        List<ValidationError> validationErrors
) {
}