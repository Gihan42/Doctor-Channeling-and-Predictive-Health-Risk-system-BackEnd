package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private Long id;
    private long medicalCenterId;
    private long doctorId;
    private long patientId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDate paymentDate;
    private LocalDateTime paymentTime;
}
