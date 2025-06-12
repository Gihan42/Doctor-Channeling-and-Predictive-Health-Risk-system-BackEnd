package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "uniq_Id", length = 255)
    @NonNull
    private String uniqId; //auto generated unique identifier ex AD-1

    @Column(name = "email", length = 255)
    @NonNull
    private String email;

    @Column(name = "password", length = 255)
    @NonNull
    private String password;

    @Column(name = "fullName", length = 255)
    @NonNull
    private String fullName;

    @Column(name = "roleId")
    @NonNull
    private long roleId;

    @Column(name = "status", length = 255)
    @NonNull
    private String status;

}
