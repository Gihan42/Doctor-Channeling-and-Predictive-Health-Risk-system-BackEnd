package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalCenterDTO {
    private Long id;
    private String centerName;
    private String registrationNumber;
    private String contact1;
    private String contact2;
    private String email;
    private String address;
    private String distric;
    private String openTime;
    private String closeTime;
    private double channelingFee;
    private long centerTypeId;
    private String status;
    private List<ChannelingRoomDTO> channelingRooms;
    private String medicalCenterType;

}
