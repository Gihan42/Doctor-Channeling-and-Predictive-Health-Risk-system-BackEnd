package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "patientReview")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientReview {
    @Id
    @Column(name = "patientReviewId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientReviewId;
    @NonNull
    @Column(name = "comment",columnDefinition = "TEXT")
    private String comment;
    @NonNull
    @Column(name = "patientId")
    private Long patientId;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;
    @NonNull
    @Column(name = "viewed", columnDefinition = "boolean default false")
    private boolean viewed = false;
    @NonNull
    @Column(name = "status")
    private String status;

}
