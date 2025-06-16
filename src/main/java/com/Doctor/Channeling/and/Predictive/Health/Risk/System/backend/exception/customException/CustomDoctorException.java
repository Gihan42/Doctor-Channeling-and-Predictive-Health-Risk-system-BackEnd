package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException;

import org.springframework.security.authentication.BadCredentialsException;

public class CustomDoctorException extends RuntimeException{
    public CustomDoctorException(String msg) {
        super(msg);
    }
}
