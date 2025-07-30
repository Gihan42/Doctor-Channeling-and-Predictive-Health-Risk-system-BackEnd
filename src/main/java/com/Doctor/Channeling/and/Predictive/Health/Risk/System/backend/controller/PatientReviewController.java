package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientReviewDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.PatientReview;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PatientReviewProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.PatientReviewService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class PatientReviewController {

    private final PatientReviewService patientReviewService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createReview(@RequestBody PatientReviewDTO dto,
                                                         @RequestAttribute String type){
        PatientReview patientReview = patientReviewService.createPatientReview(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", patientReview),
                HttpStatus.CREATED
        );
    }
    @PutMapping(params = {"patientReviewId"})
    public ResponseEntity<StandardResponse> activeReview(@RequestParam long patientReviewId,
                                                         @RequestAttribute String type){
        PatientReview patientReview =
                patientReviewService.activePatientReview(patientReviewId, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", patientReview),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getActiveReview")
    public ResponseEntity<StandardResponse> getActiveReview(@RequestAttribute String type) {
        List<PatientReviewProjection> patientReviewProjections =
                        patientReviewService.activePatientReview(type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", patientReviewProjections),
                HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"patientReviewId"})
    public ResponseEntity<StandardResponse> deleteReview(@RequestParam long patientReviewId,
                                                         @RequestAttribute String type) {
        PatientReview patientReview = patientReviewService.deletePatientReview(patientReviewId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", patientReview),
                HttpStatus.OK
        );
    }
}
