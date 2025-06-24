package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

import java.util.Date;

public interface AppointmentProjection {
    Long getId();
    Long getPatientId();
    String getPatientName();
    Long getDoctorId();
    Long getMedicleCenterId();
    Long getRoomId();
    Long getSheduleId();
    Date getAppointmentDate();
    Date getAppointmentTime();
    Date getAppointmentEndTime();
    Integer getChannelNumber();
    Date getBookingDate();
    String getAppointmentStatus();

    // From doctor (alias: d)
    String getDoctorName();
    String getDoctorGender();
    String getDoctorContact();
    String getDoctorEmail();
    String getMedicalRegistrationNo();
    Integer getYearsOfExperience();

    // From specialization (alias: s)
    String getSpecializationName();

    // From medical center (alias: mc)
    String getMedicalCenterName();
    String getRegistrationNumber();
    String getMedicalCenterContact();
    String getMedicalCenterEmail();
    String getDistric();
    Date getOpenTime();
    Date getCloseTime();
    Double getChannelingFee();

    // From medical center type (alias: mct)
    String getMedicalCenterType();
}
