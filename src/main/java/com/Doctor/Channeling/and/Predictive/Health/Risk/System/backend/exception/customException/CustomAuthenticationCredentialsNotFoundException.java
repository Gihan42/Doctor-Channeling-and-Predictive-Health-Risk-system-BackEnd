package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

public class CustomAuthenticationCredentialsNotFoundException extends AuthenticationCredentialsNotFoundException {
    public CustomAuthenticationCredentialsNotFoundException(String msg) {
        super(msg);
    }
}
