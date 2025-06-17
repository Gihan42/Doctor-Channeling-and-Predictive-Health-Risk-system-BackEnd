package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SpecializationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomSpecializationException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.SpecializationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.SpecializationService;
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
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepo specializationRepo;
    private final ModelMapper modelMapper;

    @Override
    public Specialization saveSpecialization(String specializationName) {
        String normalized = specializationName.substring(0, 1).toUpperCase() + specializationName.substring(1).toLowerCase();

        Specialization bySpecializationAndStatusActive = specializationRepo.findBySpecializationAndStatusActive(normalized);
        if(Objects.equals(bySpecializationAndStatusActive,null)){
            Specialization specialization = new Specialization(
                    (long)0,
                    normalized,
                    "Active"
            );

            return specializationRepo.save(specialization);

        } else {
            return bySpecializationAndStatusActive;
        }

    }

    @Override
    public Specialization updateSpecialization(SpecializationDTO specializationDTO, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        String normalized = specializationDTO.getSpecializationName().substring(0, 1).toUpperCase() + specializationDTO.getSpecializationName().substring(1).toLowerCase();

        Specialization byIdAndStatusActive = specializationRepo.findByIdAndStatusActive(specializationDTO.getId());
        if (!Objects.equals(byIdAndStatusActive, null)) {
            byIdAndStatusActive.setStatus("Active");
            byIdAndStatusActive.setSpecializationName(normalized);
            return specializationRepo.save(byIdAndStatusActive);
        }
        throw new CustomSpecializationException("Specialization not found or already inactive");
    }

    @Override
    public List<SpecializationDTO> getAllSpecializations(String type) {
        if (!type.equals("Admin") && !type.equals("Doctor") && !type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return modelMapper.map(specializationRepo.getAllActiveSpecializations(),new TypeToken<ArrayList<SpecializationDTO>>(){}.getType());

    }

    @Override
    public Specialization getSpecializationByName(String name, String type) {
        if (!type.equals("Admin") && !type.equals("Doctor") && !type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        String normalized = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

        Specialization specialization = specializationRepo.findBySpecializationAndStatusActive(normalized);
        if (!Objects.equals(specialization,null)){
            return specialization;
        }
        throw new CustomSpecializationException("Specialization not found or already inactive");
    }
}
