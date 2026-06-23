package com.cortis.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String messageKey,
        String path,
        List<ValidationError> validationErrors
) {
}