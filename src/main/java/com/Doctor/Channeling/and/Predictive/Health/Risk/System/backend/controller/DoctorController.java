package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SignUpDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Doctor;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.DoctorService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping(path = "/save")
    public ResponseEntity<StandardResponse> saveDoctor(@RequestBody DoctorDTO dto,
                                                       @RequestAttribute String type){
        DoctorDTO save = doctorService.saveDoctor(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", save),
                HttpStatus.CREATED
        );
    }

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updateDoctor(@RequestBody DoctorDTO dto,
                                                         @RequestAttribute String type){
        DoctorDTO update = doctorService.updateDoctor(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", update),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getDoctors")
    public ResponseEntity<StandardResponse> getAllDoctors( @RequestAttribute String type){
        List<DoctorProjection> allDoctors = doctorService.getAllDoctors(type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success",allDoctors),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"email"})
    public ResponseEntity<StandardResponse> getDoctorByEmail(@RequestParam String email,
                                                             @RequestAttribute String type){
        DoctorProjection doctorByEmail = doctorService.getDoctorEmail(email, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success",doctorByEmail),
                HttpStatus.OK
        );
    }

    @PutMapping(path = "/updatePw")
    public ResponseEntity<StandardResponse> updateDoctorPassword(@RequestBody UserPasswordDTO dto, @RequestAttribute String type){
        String message = doctorService.updateUserPassword(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"updated",message ),
                HttpStatus.OK
        );
    }
    @DeleteMapping(path = "/delete",params = {"id"})
    public ResponseEntity<StandardResponse> deleteDoctor(@RequestParam("id") long id,
                                                         @RequestAttribute String type){
        Doctor doctor = doctorService.deleteDoctor(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"deleted",doctor),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"medicleCenterId","specializationId"})
    public ResponseEntity<StandardResponse> getDoctorsByMedicalCenterId(@RequestParam("medicleCenterId") long medicleCenterId,
                                                                         @RequestParam("specializationId") long specializationId,
                                                                         @RequestAttribute String type) {
        List<Doctor> doctorsByMedicalCenterId = doctorService.findDoctorsByMedicalCenterId(medicleCenterId, specializationId,type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", doctorsByMedicalCenterId),
                HttpStatus.OK
        );
    }
}
