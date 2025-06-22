package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenterType;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterTypeRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalCenterTypeServiceImpl implements MedicalCenterTypeService {

    private final MedicalCenterTypeRepo medicalCenterTypeRepo;

    @Override
    public long addMedicalCenterType(String medicalCenterTypeName) {

        MedicalCenterType byCenterType = medicalCenterTypeRepo.findByCenterType(medicalCenterTypeName);
        if(Objects.equals(byCenterType, null)) {
            MedicalCenterType medicalCenterType = new MedicalCenterType(
                    (long)0,
                    medicalCenterTypeName,
                    "Active"
            );
            MedicalCenterType save = medicalCenterTypeRepo.save(medicalCenterType);
            return save.getId();
        }
        else{
            return byCenterType.getId();
        }

    }
}
