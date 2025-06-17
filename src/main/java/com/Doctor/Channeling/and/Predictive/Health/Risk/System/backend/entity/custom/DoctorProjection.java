package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom;

public interface DoctorProjection {
    Long getId();
    String getUniqId();
    String getFullName();
    String getGender();
    String getContact();
    String getAddress1();
    String getAddress2();
    String getNic();
    String getEmail();
    String getPassword();
    String getMedicalRegistrationNo();
    int getYearsOfExperience();
    String getHospitalAffiliation();
    String getQualificationName();
    String getSpecialization();
    String getStatus();
    double getDoctorFee();
    long getRoleId();
}
