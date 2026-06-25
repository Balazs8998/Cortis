package com.cortis.core.exception.ex;

import org.springframework.security.authentication.BadCredentialsException;

public class ChipCodeNotFoundException extends BadCredentialsException {

    public ChipCodeNotFoundException(String message) {
        super(message);
    }

}
