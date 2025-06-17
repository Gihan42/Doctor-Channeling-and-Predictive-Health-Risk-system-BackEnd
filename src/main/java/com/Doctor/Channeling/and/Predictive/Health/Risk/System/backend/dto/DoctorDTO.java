package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    private Long id;
    private String uniqId;
    private String fullName;
    private String gender;
    private String contact;
    private String address1;
    private String address2;
    private String nic;
    private String email;
    private String password;
    private String medicalRegistrationNo;
    private int yearsOfExperience;
    private String hospitalAffiliation;
    //private int qualificationId;
    //private int specializationId;
    private String qualificationName;
    private String specialization;
    private String status;
    private double doctorFee;
    private int roleId;
}
