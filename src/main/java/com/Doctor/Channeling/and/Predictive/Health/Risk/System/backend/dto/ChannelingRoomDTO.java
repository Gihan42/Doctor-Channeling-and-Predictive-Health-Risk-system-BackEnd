package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelingRoomDTO {

    private Long id;
    private long medicalCenterId;
    private String roomName;
    private String description;
    private String status;
}
