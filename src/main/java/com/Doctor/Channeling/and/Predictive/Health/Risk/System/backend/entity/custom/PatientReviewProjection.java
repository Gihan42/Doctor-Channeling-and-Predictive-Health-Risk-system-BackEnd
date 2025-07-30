package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface PatientReviewProjection {
    Long getPatientReviewId();
    String getComment();
    String getStatus();
    String getPatientName();
    java.util.Date getDate();
    boolean isViewed();
}
