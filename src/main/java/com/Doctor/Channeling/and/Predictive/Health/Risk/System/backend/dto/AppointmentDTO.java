package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {
    private Long id;
    private long patientId;
    private String patientName;
    private long doctorId;
    private long medicleCenterId;
    private long roomId;
    private long sheduleId;
    private Date appointmentDate;
    private Date appointmentTime;
    private int channelNumber;
    private Date bookingDate;
    private String paymentStatus;
}
