package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    @NotNull
    private long appointmentId;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private long patientId;

    @Column(name = "amount", nullable = false)
    @NotNull
    private double amount;

    @Column(name = "payment_method", length = 255)
    @NotNull
    private String paymentMethod;

    @Column(name = "payment_status", length = 255)
    @NotNull
    private String paymentStatus;  // e.g., PENDING, SUCCESS, FAILED, REFUNDED

    @Column(name = "payment_date")
    @NotNull
    private Date paymentDate;

    @Column(name = "payment_time")
    @NotNull
    private Date paymentTime;
}
