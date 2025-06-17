package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.QualificationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SpecializationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qualification")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class QualificationController {

    private final QualificationService qualificationService;

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updateQualification(@RequestBody QualificationDTO qualificationDTO,
                                                                @RequestAttribute String type){
        Qualification qualification = qualificationService.updateQualification(qualificationDTO, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"qualification Updated",qualification),
                HttpStatus.CREATED
        );
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<StandardResponse> getAllQualification(@RequestAttribute String type){
        List<QualificationDTO> allQualifications = qualificationService.getAllQualifications(type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded",allQualifications),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"qualification"})
    public ResponseEntity<StandardResponse> getQualificationByName(@RequestParam String qualification,@RequestAttribute String type){
        Qualification qualificationByName = qualificationService.getQualificationByName(qualification, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded",qualificationByName),
                HttpStatus.OK
        );
    }
}
