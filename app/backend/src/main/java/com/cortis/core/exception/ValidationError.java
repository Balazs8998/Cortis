package com.cortis.core.exception;

public record ValidationError(

        String field,
        String messageKey

) {
}
