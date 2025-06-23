package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface DoctorScheduleProjection {
    Long getScheduleId();
    String getDayOfWeek();
    String getStartTime();
    String getEndTime();
    String getStatus();

    Long getMedicalCenterId();
    String getCenterName();
    String getAddress();

    Long getId();
    Long getMedicalCenterIdRoom();
    String getRoomName();
    String getDescription();
    String getRoomStatus();
}
