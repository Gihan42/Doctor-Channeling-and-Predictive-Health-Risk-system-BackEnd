package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "medicle_center_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalCenterType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicle_center_type_id")
    private Long id;

    @Column(name = "center_type", length = 255)
    @NotNull
    private String centerType;

    @Column(name = "status", length = 255)
    @NotNull
    private String status;
}
