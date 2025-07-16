package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SignUpDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.PatientService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping(path = "/signUp")
    public ResponseEntity<StandardResponse> signUpPatient(@RequestBody SignUpDTO dto){
        LoginResponse loginResponse = patientService.singUp(dto);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", loginResponse),
                HttpStatus.CREATED
        );
    }

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updatePatient(@RequestBody PatientDTO adminDTO,
                                                          @RequestAttribute String type){
        Patient patient = patientService.updatePatient(adminDTO, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"Patient Updated",patient),
                HttpStatus.CREATED
        );
    }

    @GetMapping(path = "/getAllActivePatients")
    public ResponseEntity<StandardResponse> getAllActivePatients(@RequestAttribute String type) {
        return new ResponseEntity<>(
                new StandardResponse(200, "success", patientService.getAllActivePatients(type)),
                HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"id"})
    public ResponseEntity<StandardResponse> deletePatient(@RequestParam("id") long id,
                                                          @RequestAttribute String type) {
        Patient patient = patientService.deletePatient(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "Patient Deleted", patient),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"email"})
    public ResponseEntity<StandardResponse> getPatientByEmail(@RequestParam("email") String email,
                                                              @RequestAttribute String type) {
        Patient patient = patientService.getPatientByEmail(email, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", patient),
                HttpStatus.OK
        );
    }

    @PutMapping(path = "/updatePw")
    public ResponseEntity<StandardResponse> updateUserPassword(@RequestBody UserPasswordDTO dto,
                                                               @RequestAttribute String type) {
        String response = patientService.updateUserPassword(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "Password Updated", response),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getPatientNearByCity")
    public ResponseEntity<StandardResponse> getPatientNearByCity(@RequestParam("id") long id,
                                                                  @RequestAttribute String type) {
        return new ResponseEntity<>(
                new StandardResponse(200, "success", patientService.getPatientNearByCity(id, type)),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/count")
    public ResponseEntity<StandardResponse> countPatientStatus(@RequestAttribute String type) {
        long count = patientService.countPatientStatus(type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", count),
                HttpStatus.OK
        );
    }
}
