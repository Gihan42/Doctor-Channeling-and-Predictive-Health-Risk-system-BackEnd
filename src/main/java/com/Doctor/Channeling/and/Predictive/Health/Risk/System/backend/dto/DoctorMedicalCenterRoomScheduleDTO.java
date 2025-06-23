package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorMedicalCenterRoomScheduleDTO {
    private Long id;
    private long doctorId;
    private long  medicalCenterId;
    private long channelingRoomId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String status;
}
