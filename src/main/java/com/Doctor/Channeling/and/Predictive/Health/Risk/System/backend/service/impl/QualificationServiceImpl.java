package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.QualificationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SpecializationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomQualificationException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.QualificationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
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
public class QualificationServiceImpl implements QualificationService {

    private final QualificationRepo qualificationRepo;
    private final ModelMapper modelMapper;

    @Override
    public Qualification saveQualification(String qualificationName) {
        Qualification qualificationByName = qualificationRepo.findQualificationByName(qualificationName.toUpperCase());
        if(Objects.equals(qualificationByName, null)){
            Qualification   saved = new Qualification(
                    (long)0,
                    qualificationName.toUpperCase(),
                    "Active"
            );

            return  qualificationRepo.save(saved);
        }else{
            return qualificationByName;
        }

    }

    @Override
    public Qualification updateQualification(QualificationDTO qualificationDTO, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Qualification qualificationById = qualificationRepo.findQualificationById(qualificationDTO.getId());
        if (!Objects.equals(qualificationById, null)) {

            qualificationById.setStatus("Active");
            qualificationById.setQualificationName(qualificationDTO.getQualificationName().toUpperCase());
            return qualificationRepo.save(qualificationById);
        }
        throw new CustomQualificationException("Qualification not found");
    }

    @Override
    public List<QualificationDTO> getAllQualifications(String type) {
        if (!type.equals("Admin") && !type.equals("Doctor") && !type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return modelMapper.map(qualificationRepo.getAllActiveQualifications(),new TypeToken<ArrayList<QualificationDTO>>(){}.getType());

    }
    @Override
    public Qualification getQualificationByName(String qualificationName, String type) {
        if (!type.equals("Admin") && !type.equals("Doctor") && !type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Qualification qualificationByName = qualificationRepo.findQualificationByName(qualificationName.toUpperCase());
        if (!Objects.equals(qualificationByName, null)){
            return qualificationByName;
        }
        throw new CustomQualificationException("Qualification not found");

    }
}
