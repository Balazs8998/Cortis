package com.cortis.desktop.core.exception;

public class ApiException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ApiException(ErrorResponse errorResponse) {
        super(errorResponse.messageKey());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}