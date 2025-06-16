package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "specialization")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialization_id")
    private Long id;

    @Column(name = "specialization_name", length = 255)
    @NotNull
    private String specializationName;

    @Column(name = "status", length = 50)
    @NotNull
    private String status;


}
