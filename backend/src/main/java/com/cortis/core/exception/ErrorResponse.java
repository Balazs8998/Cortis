package com.cortis.core.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String messageKey,
        String path
) {
}