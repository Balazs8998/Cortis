package com.cortis.auth.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class ChipCardAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private Object credentials;


    public ChipCardAuthenticationToken(Object credentials) {
        super(null);
        this.principal = null;
        this.credentials = credentials;
        setAuthenticated(false);
    }


    public ChipCardAuthenticationToken(Object principal, Object credentials,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }


    public static ChipCardAuthenticationToken unauthenticated( Object credentials){
        return new ChipCardAuthenticationToken( credentials);
    }


    public static ChipCardAuthenticationToken authenticated(Object principal,
                                                            Object credentials,
                                                            Collection<? extends GrantedAuthority> authorities) {

        return new ChipCardAuthenticationToken(principal, credentials,
                authorities);
    }


    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
