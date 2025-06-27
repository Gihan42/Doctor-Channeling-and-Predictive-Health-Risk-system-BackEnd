package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SpecializationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterSpecializationProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.SpecializationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialization")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updateSpecialization(@RequestBody SpecializationDTO specializationDTO,
                                                                 @RequestAttribute String type){
        Specialization specialization = specializationService.updateSpecialization(specializationDTO, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"specialization Updated",specialization),
                HttpStatus.CREATED
        );
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<StandardResponse> getAllSpecialization(@RequestAttribute String type){

        List<SpecializationDTO> allSpecializations = specializationService.getAllSpecializations(type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded",allSpecializations),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"specialization"})
    public ResponseEntity<StandardResponse> getSpecializationByName(@RequestParam String specialization,@RequestAttribute String type){
        Specialization specializationByName = specializationService.getSpecializationByName(specialization, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded",specializationByName),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"specializationName"})
    public ResponseEntity<StandardResponse> findMedicalCentersBySpecialization(@RequestParam String specializationName,
                                                                               @RequestAttribute String type){
        List<MedicalCenterSpecializationProjection> medicalCentersBySpecialization =
                specializationService.findMedicalCentersBySpecialization(specializationName, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded",medicalCentersBySpecialization),
                HttpStatus.OK
        );
    }

}
