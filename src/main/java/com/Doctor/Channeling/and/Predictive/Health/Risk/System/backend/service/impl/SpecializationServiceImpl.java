package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.SpecializationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
}
