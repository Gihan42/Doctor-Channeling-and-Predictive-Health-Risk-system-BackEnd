package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SpecializationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterSpecializationProjection;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecializationService {

    Specialization saveSpecialization(String specializationName);
    Specialization updateSpecialization(SpecializationDTO specializationDTO,String type);
    List<SpecializationDTO> getAllSpecializations(String type);
    Specialization getSpecializationByName(String name,String type);
    List<MedicalCenterSpecializationProjection> findMedicalCentersBySpecialization(String specializationName,String type);
}
