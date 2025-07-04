package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Doctor;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorProjection;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorService {
    DoctorDTO saveDoctor(DoctorDTO doctorDTO,String type);
    String generateUniqueId();
    DoctorDTO updateDoctor(DoctorDTO doctorDTO,String type);
    List<DoctorProjection> getAllDoctors(String type);
    DoctorProjection getDoctorEmail(String email, String type);
    String updateUserPassword(UserPasswordDTO dto, String type);
    Doctor deleteDoctor(long id, String type);
    List<Doctor> findDoctorsByMedicalCenterId( long medicalCenterId,long specializationId,String type);
}
