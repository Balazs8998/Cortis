package com.cortis.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
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