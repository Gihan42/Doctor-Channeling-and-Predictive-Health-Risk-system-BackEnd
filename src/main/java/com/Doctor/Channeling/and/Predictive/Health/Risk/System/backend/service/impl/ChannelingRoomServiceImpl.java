package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.ChannelingRoomService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelingRoomServiceImpl implements ChannelingRoomService {

    private final MedicalCenterRepo medicalCenterRepo;
    private  final ModelMapper modelMapper;

    @Override
    public List<ChannelingRoomProjection> findAllChannelingRoomsByMedicalCenter(long id, String type) {
        if (!type.equals("Admin") && !type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return medicalCenterRepo.getAllChannelingRoomsByMedicalCenterId(id);
    }
}
