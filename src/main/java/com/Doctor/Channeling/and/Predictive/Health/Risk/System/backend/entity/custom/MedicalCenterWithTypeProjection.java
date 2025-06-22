package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface MedicalCenterWithTypeProjection {
    Long getId();
    String getCenterName();
    String getRegistrationNumber();
    String getContact1();
    String getContact2();
    String getEmail();
    String getAddress();
    String getDistric();
    String getStatus();
    Double getChannelingFee();
    Long getCenterTypeId();
    String getCenterType();
}
