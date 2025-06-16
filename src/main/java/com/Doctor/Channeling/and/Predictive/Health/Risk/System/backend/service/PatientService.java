package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SignUpDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;

import java.util.List;

public interface PatientService {
    LoginResponse singUp(SignUpDTO signUpDTO);
    String generatePatientId();
    Patient updatePatient(PatientDTO patientDTO, String type);
    List<PatientDTO> getAllActivePatients(String type);
    Patient deletePatient(long id, String type);
    Patient getPatientByEmail(String email, String type);
    String updateUserPassword(UserPasswordDTO dto, String type);

}
