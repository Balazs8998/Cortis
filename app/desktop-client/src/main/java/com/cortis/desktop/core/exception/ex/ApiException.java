package com.cortis.desktop.core.exception.ex;

import com.cortis.desktop.core.exception.ErrorResponse;

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