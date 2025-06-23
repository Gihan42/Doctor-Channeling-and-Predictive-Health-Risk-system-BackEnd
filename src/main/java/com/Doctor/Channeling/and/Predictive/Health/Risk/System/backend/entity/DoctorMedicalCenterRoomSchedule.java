package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "doctor_medical_center_room_schedule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorMedicalCenterRoomSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_medical_center_room_schedule_id")
    private Long id;

    @Column(name = "doctor_id")
    @NotNull
    private long doctorId;

    @Column(name = "medical_center_id")
    @NotNull
    private long  medicalCenterId;

    @Column(name = "channeling_room_id")
    @NotNull
    private long channelingRoomId;

    @Column(name = "day_of_week", length = 255)
    @NotNull
    private String dayOfWeek;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "status", length = 255)
    @NotNull
    private String status;
}
