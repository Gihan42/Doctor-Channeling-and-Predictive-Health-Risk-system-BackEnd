package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface AppointmentDetailsForDashBoardProjection {
    String getDoctorName();
    int getChannelingNumber();
    Long getChannelingRoomId();
    java.util.Date getAppointmentDate();
    java.util.Date getAppointmentTime();
    String getMedicalCenterName();
    String getAppointmentStatus();
}
