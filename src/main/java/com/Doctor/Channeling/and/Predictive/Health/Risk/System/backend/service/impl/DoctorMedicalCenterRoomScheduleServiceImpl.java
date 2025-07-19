package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorMedicalCenterRoomScheduleDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorScheduleProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ScheduleProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomDoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorMedicalCenterRoomScheduleRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.DoctorMedicalCenterRoomScheduleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorMedicalCenterRoomScheduleServiceImpl implements DoctorMedicalCenterRoomScheduleService {

    private final ModelMapper modelMapper;
    private final DoctorMedicalCenterRoomScheduleRepo doctorMedicalCenterRoomScheduleRepo;


    @Override
    public DoctorMedicalCenterRoomSchedule createDoctorChannelingRoomSchedule(DoctorMedicalCenterRoomScheduleDTO
                                                                                          doctorMedicalCenterRoomScheduleDTO,
                                                                              String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }

        DoctorMedicalCenterRoomSchedule byIdAndStatusActive =
                doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(doctorMedicalCenterRoomScheduleDTO.getDoctorId());

        if(Objects.equals(byIdAndStatusActive,null)){
            DoctorMedicalCenterRoomSchedule map =
                    modelMapper.map(doctorMedicalCenterRoomScheduleDTO, DoctorMedicalCenterRoomSchedule.class);
            map.setStatus("Active");
            return doctorMedicalCenterRoomScheduleRepo.save(map);

        }
        throw new CustomDoctorMedicalCenterRoomSchedule("Doctor Medical Center Room Schedule already exists for this doctor");

    }

    @Override
    public DoctorMedicalCenterRoomSchedule updateDoctorMedicalCenterRoomSchedule(DoctorMedicalCenterRoomScheduleDTO doctorMedicalCenterRoomScheduleDTO,
                                                                                 String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        DoctorMedicalCenterRoomSchedule byIdAndStatusActive =
                doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(doctorMedicalCenterRoomScheduleDTO.getId());
        if(!Objects.equals(byIdAndStatusActive,null)){

            DoctorMedicalCenterRoomSchedule map =
                    modelMapper.map(doctorMedicalCenterRoomScheduleDTO, DoctorMedicalCenterRoomSchedule.class);
            map.setStatus("Active");
            return doctorMedicalCenterRoomScheduleRepo.save(map);

        }
        throw new CustomDoctorMedicalCenterRoomSchedule("Doctor Medical Center Room Schedule already exists for this doctor");
    }

    @Override
    public List<DoctorScheduleProjection> getDoctorScheduleWithRoomDetails(long doctorId, String type) {
        if (!type.equals("Admin") && !type.equals("Patient") ){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorMedicalCenterRoomScheduleRepo.getDoctorScheduleWithRoomDetails(doctorId);
    }

    @Override
    public List<DoctorScheduleProjection> getDoctorScheduleWithRoomDetailsByMedicalCenterId(long medicalCenterId, String type) {
        if (!type.equals("Admin") && !type.equals("Patient") ){
            throw new CustomBadCredentialsException("dont have permission");
        }

        return doctorMedicalCenterRoomScheduleRepo.getDoctorScheduleWithRoomDetailsByMedicalCenterId(medicalCenterId);
    }

    @Override
    public DoctorMedicalCenterRoomSchedule deleteDoctorScheduleWithRoomDetails(long id, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
    DoctorMedicalCenterRoomSchedule byIdAndStatusActive =
                doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(id);
    if (!Objects.equals(byIdAndStatusActive, null)) {
            byIdAndStatusActive.setStatus("InActive");
            return doctorMedicalCenterRoomScheduleRepo.save(byIdAndStatusActive);
    }
        throw new CustomDoctorMedicalCenterRoomSchedule("Doctor Medical Center Room Schedule already exists for this doctor");

    }

    @Override
    public List<String> findDayOfWeekByDoctorIdAndMedicalCenterId(long doctorId, long medcleCenterId, String type) {
        if (!type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorMedicalCenterRoomScheduleRepo.findDayOfWeekByDoctorIdAndMedicalCenterId(doctorId, medcleCenterId);
    }

    @Override
    public List<String> findStartTimeByDoctorId(long doctorId, String type) {
        if (!type.equals("Patient") ){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorMedicalCenterRoomScheduleRepo.findStartTimeByDoctorId(doctorId);
    }

    @Override
    public List<ScheduleProjection> findAllActiveDoctorSchedules(String type) {
        if (!type.equals("Admin") ){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorMedicalCenterRoomScheduleRepo.findAllActiveDoctorSchedules();
    }
}
