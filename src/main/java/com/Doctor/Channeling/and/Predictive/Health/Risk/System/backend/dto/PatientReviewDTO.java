package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientReviewDTO {

    private Long patientReviewId;
    private String comment;
    private Long patientId;
    private Date date;
    private boolean viewed = false;
    private String status;
}
