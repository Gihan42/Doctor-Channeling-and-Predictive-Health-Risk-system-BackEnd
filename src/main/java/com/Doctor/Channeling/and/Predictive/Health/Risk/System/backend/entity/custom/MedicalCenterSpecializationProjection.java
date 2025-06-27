package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface MedicalCenterSpecializationProjection {
    String getSpecializationName();
    Long getMedicleCenterId();
    String getCenterName();
    String getContact1();
    String getContact2();
    String getEmail();
    String getAddress();
    String getDistric();
    String getOpenTime();
    String getCloseTime();
    Double getChannelingFee();
    String getCenterType();
}
