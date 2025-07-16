package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface TodayAppointmentProjection {
    String getDoctorName();
    String getAppointmentTime();
    String getMedicalCenterName();
    String getStartTime();
    String getEndTime();
}
