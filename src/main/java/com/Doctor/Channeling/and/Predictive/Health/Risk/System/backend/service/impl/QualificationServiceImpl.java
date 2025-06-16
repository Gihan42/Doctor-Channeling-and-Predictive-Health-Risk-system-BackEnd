package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.QualificationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class QualificationServiceImpl implements QualificationService {

    private final QualificationRepo qualificationRepo;
    private final ModelMapper modelMapper;

}
