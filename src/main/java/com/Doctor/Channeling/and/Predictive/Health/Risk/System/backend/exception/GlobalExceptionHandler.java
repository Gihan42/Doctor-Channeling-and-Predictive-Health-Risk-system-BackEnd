package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.*;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomBadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(CustomBadCredentialsException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomAuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<String> handleAuthenticationCredentials(CustomAuthenticationCredentialsNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomAdminException.class)
    public ResponseEntity<String> handleAdminException(CustomAdminException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomPatientException.class)
    public ResponseEntity<String> handlePatientException(CustomPatientException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler({Exception.class})
    public ResponseEntity<StandardResponse> commonExceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new StandardResponse(422, "Operation Filed",  e.getMessage()));
    }

    @ExceptionHandler(CustomDoctorException.class)
    public ResponseEntity<String> handleDoctorException(CustomDoctorException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomQualificationException.class)
    public ResponseEntity<String> handleQualificationException(CustomQualificationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomSpecializationException.class)
    public ResponseEntity<String> handleSpecializationException(CustomSpecializationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomMedicalCenterException.class)
    public ResponseEntity<String> handleMedicalCenterException(CustomMedicalCenterException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomDoctorMedicalCenterRoomSchedule.class)
    public ResponseEntity<String> handleDoctorMedicalCenterRoomSchedule(CustomDoctorMedicalCenterRoomSchedule ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomPaymentException.class)
    public ResponseEntity<String> handlePaymentException(CustomPaymentException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomAppointmentException.class)
    public ResponseEntity<String> handleAppointmentException(CustomAppointmentException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomPatientReviewException.class)
    public ResponseEntity<String> handlePatientReviewException(CustomPatientReviewException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
