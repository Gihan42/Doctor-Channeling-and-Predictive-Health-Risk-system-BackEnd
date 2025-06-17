package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.QualificationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class QualificationServiceImpl implements QualificationService {

    private final QualificationRepo qualificationRepo;
    private final ModelMapper modelMapper;

    @Override
    public Qualification saveQualification(String qualificationName) {
        String normalized = qualificationName.substring(0, 1).toUpperCase() + qualificationName.substring(1).toLowerCase();
        Qualification qualificationByName = qualificationRepo.findQualificationByName(normalized);
        if(Objects.equals(qualificationByName, null)){
            Qualification   saved = new Qualification(
                    (long)0,
                    normalized,
                    "Active"
            );

            return  qualificationRepo.save(saved);
        }else{
            return qualificationByName;
        }

    }
}
