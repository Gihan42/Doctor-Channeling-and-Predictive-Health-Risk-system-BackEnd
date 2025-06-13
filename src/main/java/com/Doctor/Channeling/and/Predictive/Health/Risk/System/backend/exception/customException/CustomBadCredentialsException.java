package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException;

import org.springframework.security.authentication.BadCredentialsException;

public class CustomBadCredentialsException extends BadCredentialsException {
    public CustomBadCredentialsException(String msg) {
        super(msg);
    }
}
