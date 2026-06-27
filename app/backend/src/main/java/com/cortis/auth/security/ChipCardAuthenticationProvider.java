package com.cortis.auth.security;

import com.cortis.core.exception.ex.ChipCodeNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ChipCardAuthenticationProvider implements AuthenticationProvider {


    private final CortisUserDetailsService userDetailsService;

    public ChipCardAuthenticationProvider(CortisUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String chipCode = (String) authentication.getCredentials();

        if (chipCode == null || chipCode.isBlank()) {
            throw new ChipCodeNotFoundException("Invalid ChipCode");
        }

        CortisUserPrincipal userPrincipal = userDetailsService.loadUserByChipCode(chipCode);

        if (!userPrincipal.isEnabled()){
            throw new DisabledException("User account is disabled");
        }

        ChipCardAuthenticationToken authenticationToken =
                new ChipCardAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities()
                );

        authenticationToken.setDetails(authentication.getDetails());


        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        if (authentication.isAssignableFrom(ChipCardAuthenticationToken.class)) {
            return true;
        }
        return false;
    }
}
