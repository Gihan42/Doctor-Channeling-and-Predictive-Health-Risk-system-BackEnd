package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private Long id;
    private long appointmentId;
    private long patientId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private Date paymentDate;
    private Date paymentTime;
}
