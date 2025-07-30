package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface PaymentSummaryProjection {
    Long getPaymentId();
    Long getPatientId();
    String getPatientName();
    Long getMedicalCenterId();
    String getMedicalCenterName();
    Double getPaidAmount();
    java.time.LocalDate getPaymentDate();
}
