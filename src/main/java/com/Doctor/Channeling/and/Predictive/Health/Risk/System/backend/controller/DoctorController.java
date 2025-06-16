package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

}
