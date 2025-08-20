package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientReviewDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.PatientReview;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PatientReviewProjection;

import java.util.List;

public interface PatientReviewService {

    PatientReview createPatientReview(PatientReviewDTO patientReview,String type);

    PatientReview activePatientReview(long patientReviewId, String type) ;

    PatientReview inActivePatientReview(long patientReviewId, String type) ;

    List<PatientReviewProjection> activePatientReview(String type);

    PatientReview deletePatientReview(long patientReviewId, String type);

    List<PatientReviewProjection>  getAllPatientReviews();
}
