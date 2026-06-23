package com.cortis.desktop.core.exception;

public record ValidationError(

        String field,
        String messageKey

) {
}

