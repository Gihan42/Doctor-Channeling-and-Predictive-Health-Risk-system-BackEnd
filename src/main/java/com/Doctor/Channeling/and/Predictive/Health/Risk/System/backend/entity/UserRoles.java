package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "user_roles")
public class UserRoles {
    @Id
    @Column(name = "userRoleid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRoleId;
    @Column(name = "userRole")
    @NonNull
    private String role;
    @Column(name = "status")
    @NonNull
    private String status;
}
