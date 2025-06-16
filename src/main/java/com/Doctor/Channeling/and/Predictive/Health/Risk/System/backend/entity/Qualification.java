package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "qualification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_id")
    private Long id;

    @Column(name = "qualification_name")
    @NonNull
    private String qualificationName;

    @Column(name = "status")
    @NotNull
    private String status ;
}
