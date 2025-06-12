package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private Long id;
    private String uniqId;
    private String fullName;
    private String email;
    private String password;
    private String gender;
    private Date dateOfBirthDay;
    private int age;
    private String contact;
    private String address;
    private String status;
    private long roleId;
}
