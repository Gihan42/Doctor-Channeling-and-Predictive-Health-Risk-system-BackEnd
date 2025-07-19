package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface ScheduleProjection {
    Long getScheduleId();
    String getDayOfWeek();
    String getStartTime();
    String getEndTime();
    String getScheduleStatus();
    String getDoctorName();
    String getMedicalCenterName();
    String getRoomName();
}
