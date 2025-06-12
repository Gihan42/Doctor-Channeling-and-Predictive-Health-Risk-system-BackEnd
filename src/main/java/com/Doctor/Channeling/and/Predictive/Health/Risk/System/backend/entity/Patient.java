package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "patient")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @Column(name = "uniq_Id", length = 255)
    @NonNull
    private String uniqId; //auto generated unique identifier ex PT-1

    @Column(name = "fullName", length = 255)
    @NotNull
    private String fullName;

    @Column(name = "email", length = 255)
    @NotNull
    private String email;

    @Column(name = "password", length = 255)
    @NotNull
    private String password;

    @Column(name = "gender", length = 255)
    @NotNull
    private String gender;

    @Column(name = "dateOfBirthDay")
    @NotNull
    private Date dateOfBirthDay;

    @Column(name = "age")
    @NotNull
    private int age;

    @Column(name = "contact", length = 255)
    @NotNull
    private String contact;

    @Column(name = "address", length = 255)
    @NotNull
    private String address;

    @Column(name = "status", length = 255)
    @NotNull
    private String status;

    @Column(name = "roleId")
    @NotNull
    private long roleId;
}
