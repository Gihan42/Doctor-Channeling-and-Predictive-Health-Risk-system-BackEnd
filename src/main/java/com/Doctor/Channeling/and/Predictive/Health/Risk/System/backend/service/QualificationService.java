package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.QualificationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;

import java.util.List;

public interface QualificationService {

    Qualification saveQualification(String qualificationName);
    Qualification updateQualification(QualificationDTO qualificationDTO, String type);
    List<QualificationDTO> getAllQualifications(String type);
    Qualification getQualificationByName(String qualificationName,String type);
}
