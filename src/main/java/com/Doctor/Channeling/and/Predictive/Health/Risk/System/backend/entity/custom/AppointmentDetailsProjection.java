package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

import java.util.Date;

public interface AppointmentDetailsProjection {
    Long getAppointmentId();

    Long getPatientId();

    String getPatientName();

    Long getDoctorId();

    String getDoctorName();

    String getGender();

    String getDoctorContact();

    String getDoctorEmail();

    String getSpecializationName();

    Long getMedicleCenterId();

    String getMedicalCenterName();

    String getMedicalCenterAddress();

    String getMedicalCenterContact1();

    String getMedicalCenterContact2();

    String getMedicalCenterEmail();

    String getDistric();

    Long getRoomId();

    Long getSheduleId();

    Date getAppointmentDate();

    Date getAppointmentTime();

    Integer getChannelNumber();

    Date getBookingDate();

    String getAppointmentStatus();

    Long getPaymentId();

    Double getPaymentAmount();

    Date getPaymentDate();
}
