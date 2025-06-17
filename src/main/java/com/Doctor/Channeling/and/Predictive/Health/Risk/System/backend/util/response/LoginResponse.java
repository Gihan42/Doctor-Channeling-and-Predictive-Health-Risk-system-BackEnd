package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private int code;
    private String userName;
    private String message;
    private String role;
    private String jwt;
    private String userId;
    private  long id;
    private String email;
}
