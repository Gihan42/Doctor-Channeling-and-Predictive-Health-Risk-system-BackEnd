package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "doctor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long id;


    @Column(name = "uniq_Id", length = 255)
    @NonNull
    private String uniqId; //auto generated unique identifier ex DO-1

    @Column(name = "fullName", length = 255)
    @NonNull
    private String fullName;


    @Column(name = "gender", length = 255)
     @NonNull
    private String gender;

    @Column(name = "contact", length = 255)
     @NonNull
    private String contact;

    @Column(name = "address1", length = 255)
     @NonNull
    private String address1;

    @Column(name = "address2", length = 255)
     @NonNull
    private String address2;

    @Column(name = "nic", length = 255)
     @NonNull
    private String nic;

    @Column(name = "email", length = 255)
     @NonNull
    private String email;


    @Column(name = "password", length = 255)
     @NonNull
    private String password;

    @Column(name = "medical_Registration_No", length = 255)
     @NonNull
    private String medicalRegistrationNo;

    @Column(name = "years_Of_Experience")
     @NonNull
    private int yearsOfExperience;

    @Column(name = "hospital_Affiliation", length = 255)
     @NonNull
    private String hospitalAffiliation;

    @Column(name = "qualification_Id")
     @NonNull
    private int qualificationId;

    @Column(name = "specialization_Id")
     @NonNull
    private int specializationId;

    @Column(name = "status", length = 255)
     @NonNull
    private String status;

    @Column(name = "doctorFee")
     @NonNull
    private double doctorFee;

    @Column(name = "roleId")
     @NonNull
    private int roleId;
}
