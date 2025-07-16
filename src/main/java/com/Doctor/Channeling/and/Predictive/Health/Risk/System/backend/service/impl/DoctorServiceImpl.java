package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Doctor;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomAdminException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomDoctorException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.DoctorService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.SpecializationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepo doctorRepo;
    private  final ModelMapper modelMapper;
    private  final QualificationService qualificationService;
    private final SpecializationService  SpecializationService;
    private final UserRolesRepo rolesRepo;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;


    @Override
    public DoctorDTO saveDoctor(DoctorDTO doctorDTO, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }

        Doctor byEmail = doctorRepo.findByEmail(doctorDTO.getEmail());
        long roleIdByRole = rolesRepo.getRoleIdByRole("Doctor");
        if (Objects.equals(byEmail,null)){

            Qualification qualification = qualificationService.saveQualification(doctorDTO.getQualificationName());
            Specialization specialization = SpecializationService.saveSpecialization(doctorDTO.getSpecialization());
            Doctor map = modelMapper.map(doctorDTO, Doctor.class);
            map.setQualificationId(qualification.getId());
            map.setSpecializationId(specialization.getId());
            map.setRoleId(roleIdByRole);
            map.setUniqId(generateUniqueId());
            map.setStatus("Active");
            map.setPassword( BCrypt.hashpw(doctorDTO.getPassword(), BCrypt.gensalt(10)));
            Doctor savedDoctor = doctorRepo.save(map);
            DoctorDTO returnMap = modelMapper.map(savedDoctor, DoctorDTO.class);
            returnMap.setQualificationName(qualification.getQualificationName());
            returnMap.setSpecialization(specialization.getSpecializationName());
            return returnMap;

        }

        throw new CustomDoctorException("Doctor already exists");
    }

    @Override
    public String generateUniqueId() {
        long countActiveDoctors = doctorRepo.countActiveDoctors();
        return "DOC-"+(countActiveDoctors+1);
    }

    @Override
    public DoctorDTO updateDoctor(DoctorDTO doctorDTO, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }

        Doctor byIdAndStatus = doctorRepo.findByIdAndStatus(doctorDTO.getId());
        if (!Objects.equals(byIdAndStatus,null)){

            Qualification qualification = qualificationService.saveQualification(doctorDTO.getQualificationName());
            Specialization specialization = SpecializationService.saveSpecialization(doctorDTO.getSpecialization());
            Doctor map = modelMapper.map(doctorDTO, Doctor.class);
            map.setQualificationId(qualification.getId());
            map.setSpecializationId(specialization.getId());
            map.setRoleId(byIdAndStatus.getRoleId());
            map.setUniqId(byIdAndStatus.getUniqId());
            map.setStatus("Active");
            map.setPassword(byIdAndStatus.getPassword());
            Doctor savedDoctor = doctorRepo.save(map);
            DoctorDTO returnMap = modelMapper.map(savedDoctor, DoctorDTO.class);
            returnMap.setQualificationName(qualification.getQualificationName());
            returnMap.setSpecialization(specialization.getSpecializationName());
            return returnMap;

        }

        throw new CustomDoctorException("Doctor not found or already inactive");

    }

    @Override
    public List<DoctorProjection> getAllDoctors(String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorRepo.findAllActiveDoctors();
    }

    @Override
    public DoctorProjection getDoctorEmail(String email, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorRepo.findAllActiveDoctorsByEmail(email);
    }

    @Override
    public String updateUserPassword(UserPasswordDTO dto, String type) {
        if (!type.equals("Doctor")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Doctor byIdAndStatus = doctorRepo.findByIdAndStatus(dto.getUserId());
        boolean checkpw = BCrypt.checkpw(dto.getCurrentPassword(), byIdAndStatus.getPassword());
        if(checkpw){
            byIdAndStatus.setPassword(BCrypt.hashpw(dto.getNewPassword(),BCrypt.gensalt(10)));
            return "password change";
        }
        throw new CustomDoctorException("doctor current password is wrong! ");
    }

    @Override
    public Doctor deleteDoctor(long id, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Doctor byIdAndStatus = doctorRepo.findByIdAndStatus(id);
        if (!Objects.equals(byIdAndStatus, null)) {
            byIdAndStatus.setStatus("Inactive");
            return doctorRepo.save(byIdAndStatus);
        }
        throw new CustomDoctorException("Doctor not found or already inactive");

    }

    @Override
    public List<Doctor> findDoctorsByMedicalCenterId(long medicalCenterId,long specializationId,String type) {
        if (!type.equals("Patient") && !type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorRepo.findDoctorsByMedicalCenterId(medicalCenterId, specializationId);
    }

    @Override
    public int countDoctorsByStatusActive(String type) {
        if ( !type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return doctorRepo.countDoctorsByStatusActive();
    }
}
