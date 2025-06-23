package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorMedicalCenterRoomScheduleDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorScheduleProjection;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorMedicalCenterRoomScheduleService {

    DoctorMedicalCenterRoomSchedule createDoctorChannelingRoomSchedule(DoctorMedicalCenterRoomScheduleDTO doctorMedicalCenterRoomScheduleDTO, String type);
    DoctorMedicalCenterRoomSchedule updateDoctorMedicalCenterRoomSchedule(DoctorMedicalCenterRoomScheduleDTO doctorMedicalCenterRoomScheduleDTO, String type);
    List<DoctorScheduleProjection> getDoctorScheduleWithRoomDetails(long doctorId,String type);
    List<DoctorScheduleProjection> getDoctorScheduleWithRoomDetailsByMedicalCenterId(long medicalCenterId,String type);
    DoctorMedicalCenterRoomSchedule deleteDoctorScheduleWithRoomDetails(long id, String type);

}
