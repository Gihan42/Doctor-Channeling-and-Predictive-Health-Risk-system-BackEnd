package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.DoctorService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepo doctorRepo;
    private  final ModelMapper modelMapper;
    private  final QualificationService qualificationService;
    private final SpecializationService  SpecializationService;

}
