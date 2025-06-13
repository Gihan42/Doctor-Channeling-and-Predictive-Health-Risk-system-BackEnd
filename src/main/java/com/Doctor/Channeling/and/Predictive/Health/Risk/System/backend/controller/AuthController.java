package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AuthenticationRequestDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AuthUserService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;


    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody AuthenticationRequestDTO dto){
        LoginResponse response = authUserService.logUser(dto);
        return new ResponseEntity<LoginResponse>(
                response,
                HttpStatus.OK
        );
    }
}
