package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Doctor;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomDoctorException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.QualificationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.SpecializationService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepo doctorRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private QualificationService qualificationService;
    @Mock
    private SpecializationService specializationService;
    @Mock
    private UserRolesRepo rolesRepo;

    // These unused mocks are included because they are dependencies in the service constructor
    @Mock private com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.JWTUtil jwtUtil;
    @Mock private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @Mock private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private DoctorDTO doctorDTO;
    private Doctor doctor;
    private Qualification qualification;
    private Specialization specialization;
    private static final String ADMIN_ROLE = "Admin";
    private static final String PATIENT_ROLE = "Patient";
    private static final String DOCTOR_ROLE = "Doctor";

    @BeforeEach
    void setUp() {
        doctorDTO = new DoctorDTO();
        doctorDTO.setId(1L);
        doctorDTO.setEmail("newdoc@test.com");
        doctorDTO.setPassword("password123");
        doctorDTO.setFullName("Dr. Test");
        doctorDTO.setQualificationName("MBBS");
        doctorDTO.setSpecialization("Cardiology");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("newdoc@test.com");
        doctor.setUniqId("DOC-1");
        doctor.setPassword(BCrypt.hashpw("currentPassword", BCrypt.gensalt(10)));
        doctor.setStatus("Active");

        qualification = new Qualification(1L, "MBBS", "Medical Bachelor and Bachelor of Surgery");
        specialization = new Specialization(1L, "Cardiology", "Heart related specialization");
    }

    // ================== saveDoctor Tests ==================

    @Test
    @DisplayName("Save Doctor: Should successfully save a new doctor")
    void saveDoctor_Success() {
        // Arrange
        when(doctorRepo.findByEmail(anyString())).thenReturn(null);
        when(rolesRepo.getRoleIdByRole("Doctor")).thenReturn(2L);
        when(qualificationService.saveQualification(anyString())).thenReturn(qualification);
        when(specializationService.saveSpecialization(anyString())).thenReturn(specialization);
        when(modelMapper.map(any(DoctorDTO.class), eq(Doctor.class))).thenReturn(doctor);
        when(doctorRepo.countActiveDoctors()).thenReturn(5L); // For generating unique ID
        when(doctorRepo.save(any(Doctor.class))).thenReturn(doctor);
        when(modelMapper.map(any(Doctor.class), eq(DoctorDTO.class))).thenReturn(doctorDTO);

        // Act
        DoctorDTO savedDoctorDTO = doctorService.saveDoctor(doctorDTO, ADMIN_ROLE);

        // Assert
        ArgumentCaptor<Doctor> doctorArgumentCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepo).save(doctorArgumentCaptor.capture());
        Doctor capturedDoctor = doctorArgumentCaptor.getValue();

        assertNotNull(savedDoctorDTO);
        assertEquals("DOC-6", capturedDoctor.getUniqId());
        assertEquals("Active", capturedDoctor.getStatus());
        assertTrue(BCrypt.checkpw("password123", capturedDoctor.getPassword()));
    }

    @Test
    @DisplayName("Save Doctor: Should fail if doctor already exists")
    void saveDoctor_FailsWhenDoctorExists() {
        // Arrange
        when(doctorRepo.findByEmail(anyString())).thenReturn(doctor);

        // Act & Assert
        assertThrows(CustomDoctorException.class, () -> {
            doctorService.saveDoctor(doctorDTO, ADMIN_ROLE);
        });
    }

    @Test
    @DisplayName("Save Doctor: Should fail for non-admin user")
    void saveDoctor_FailsForNonAdmin() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            doctorService.saveDoctor(doctorDTO, PATIENT_ROLE);
        });
    }

    // ================== updateUserPassword Tests ==================

    @Test
    @DisplayName("Update Password: Should succeed with correct current password")
    void updateUserPassword_Success() {
        // Arrange
        UserPasswordDTO passwordDTO = new UserPasswordDTO();
        passwordDTO.setUserId(1L);
        passwordDTO.setCurrentPassword("currentPassword");
        passwordDTO.setNewPassword("newPassword123");

        when(doctorRepo.findByIdAndStatus(1L)).thenReturn(doctor);

        // Act
        String result = doctorService.updateUserPassword(passwordDTO, DOCTOR_ROLE);

        // Assert
        assertEquals("password change", result);
        // Verify the password on the entity was updated
        assertTrue(BCrypt.checkpw("newPassword123", doctor.getPassword()));
    }

    @Test
    @DisplayName("Update Password: Should fail with incorrect current password")
    void updateUserPassword_FailsForWrongPassword() {
        // Arrange
        UserPasswordDTO passwordDTO = new UserPasswordDTO();
        passwordDTO.setUserId(1L);
        passwordDTO.setCurrentPassword("wrongPassword");
        passwordDTO.setNewPassword("newPassword123");

        when(doctorRepo.findByIdAndStatus(1L)).thenReturn(doctor);

        // Act & Assert
        assertThrows(CustomDoctorException.class, () -> {
            doctorService.updateUserPassword(passwordDTO, DOCTOR_ROLE);
        });
    }

    // ================== deleteDoctor Tests ==================

    @Test
    @DisplayName("Delete Doctor: Should succeed by setting status to Inactive")
    void deleteDoctor_Success() {
        // Arrange
        when(doctorRepo.findByIdAndStatus(1L)).thenReturn(doctor);
        when(doctorRepo.save(any(Doctor.class))).thenReturn(doctor);

        // Act
        Doctor deletedDoctor = doctorService.deleteDoctor(1L, ADMIN_ROLE);

        // Assert
        assertNotNull(deletedDoctor);
        assertEquals("Inactive", deletedDoctor.getStatus());
        verify(doctorRepo, times(1)).save(doctor);
    }

    @Test
    @DisplayName("Delete Doctor: Should fail if doctor not found")
    void deleteDoctor_FailsWhenNotFound() {
        // Arrange
        when(doctorRepo.findByIdAndStatus(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomDoctorException.class, () -> {
            doctorService.deleteDoctor(1L, ADMIN_ROLE);
        });
    }

    // ================== findDoctorsByMedicalCenterId Tests ==================

    @Test
    @DisplayName("Find Doctors by Center: Should succeed for Patient and Admin")
    void findDoctorsByMedicalCenterId_SuccessForAuthorizedRoles() {
        // This test covers both Patient and Admin roles in one go for simplicity.
        // A ParameterizedTest could also be used here.

        // Arrange
        List<Doctor> doctorList = Collections.singletonList(doctor);
        when(doctorRepo.findDoctorsByMedicalCenterId(1L, 2L)).thenReturn(doctorList);

        // Act - Patient
        List<Doctor> resultPatient = doctorService.findDoctorsByMedicalCenterId(1L, 2L, PATIENT_ROLE);
        // Act - Admin
        List<Doctor> resultAdmin = doctorService.findDoctorsByMedicalCenterId(1L, 2L, ADMIN_ROLE);

        // Assert
        assertNotNull(resultPatient);
        assertEquals(doctorList, resultPatient);

        assertNotNull(resultAdmin);
        assertEquals(doctorList, resultAdmin);

        verify(doctorRepo, times(2)).findDoctorsByMedicalCenterId(1L, 2L);
    }
}
