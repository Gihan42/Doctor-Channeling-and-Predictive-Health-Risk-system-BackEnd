package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientReviewDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.PatientReview;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PatientReviewProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomPatientReviewException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PatientRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PatientReviewRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.PatientReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientReviewServiceImpl implements PatientReviewService {

    private final ModelMapper modelMapper;
    private final PatientReviewRepo patientReviewRepo;

    @Override
    public PatientReview createPatientReview(PatientReviewDTO patientReview, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        PatientReview byPatientReviewId =
                patientReviewRepo.findByPatientReviewId(patientReview.getPatientReviewId());
         if(Objects.equals(byPatientReviewId, null)) {
            PatientReview patientReviewEntity = modelMapper.map(patientReview, PatientReview.class);
            patientReviewEntity.setStatus("Active");
            return patientReviewRepo.save(patientReviewEntity);
         }
         throw new CustomPatientReviewException("Comment already exists for this patient review ID ");
    }

    @Override
    public PatientReview activePatientReview(long patientReviewId, String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        PatientReview byPatientReviewId =
                patientReviewRepo.findByPatientReviewId(patientReviewId);
        if(!Objects.equals(byPatientReviewId, null)) {
            int countReview = patientReviewRepo.countViewedPatientReview();
            if(countReview <= 2) {
                byPatientReviewId.setViewed(true);
                return patientReviewRepo.save(byPatientReviewId);
            } else {
                throw new CustomPatientReviewException("You have already viewed 3 reviews, you cannot view more.");
            }
        }
        throw new CustomPatientReviewException("Comment already exists for this patient review ID ");
    }

    @Override
    public PatientReview inActivePatientReview(long patientReviewId, String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        PatientReview byPatientReviewId =
                patientReviewRepo.findByPatientReviewId(patientReviewId);
        if(!Objects.equals(byPatientReviewId, null)) {
            if(byPatientReviewId.isViewed()) {
                byPatientReviewId.setViewed(false);
                return patientReviewRepo.save(byPatientReviewId);
            } else {
                throw new CustomPatientReviewException("You have already Inactive this review, you cannot inactive again.");
            }
        }
        throw new CustomPatientReviewException("Comment already exists for this patient review ID ");
    }

    @Override
    public List<PatientReviewProjection> activePatientReview(String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return patientReviewRepo.getAllPatientReviewsWithPatientName();
    }



    @Override
    public PatientReview deletePatientReview(long patientReviewId, String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
    PatientReview byPatientReviewId =
                patientReviewRepo.findByPatientReviewId(patientReviewId);
    if (!Objects.equals(byPatientReviewId, null)) {
            byPatientReviewId.setStatus("Inactive");
            return patientReviewRepo.save(byPatientReviewId);
    }
    throw new CustomPatientReviewException("Comment already exists for this patient review ID: ");

    }

    @Override
    public List<PatientReviewProjection> getAllPatientReviews() {
//        if (!type.equals("Admin")) {
//            throw new CustomBadCredentialsException("dont have permission");
//        }
        return patientReviewRepo.getAllPatientReviews();
    }
}
