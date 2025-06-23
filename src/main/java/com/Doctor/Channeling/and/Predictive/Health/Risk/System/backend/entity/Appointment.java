package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "appointment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private long patientId;  // FK to patient

    @Column(name = "patient_name", length = 255)
    @NotNull
    private String patientName;

    @Column(name = "doctor_id", nullable = false)
    @NotNull
    private long doctorId;  // FK to doctor

    @Column(name = "medicle_center_id", nullable = false)
    @NotNull
    private long medicleCenterId;  // FK to medical center

    @Column(name = "room_id", nullable = false)
    @NotNull
    private long roomId;  // FK to room

    @Column(name = "shedule_id", nullable = false)
    @NotNull
    private long sheduleId;  // FK to schedule

    @Column(name = "appointment_date")
    @NotNull
    private Date appointmentDate;

    @Column(name = "appointment_time")
    @NotNull
    private Date appointmentTime;

    @Column(name = "channel_number")
    @NotNull
    private int channelNumber;

    @Column(name = "booking_date")
    @NotNull
    private Date bookingDate;

    @Column(name = "payment_status", length = 255)
    @NotNull
    private String paymentStatus;
}
