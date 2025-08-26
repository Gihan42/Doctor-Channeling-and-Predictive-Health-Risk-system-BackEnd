package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.QualificationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomQualificationException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.QualificationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.QualificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.lang.reflect.Type;

@ExtendWith(MockitoExtension.class)
class QualificationServiceImplTest {

    @Mock
    private QualificationRepo qualificationRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private QualificationServiceImpl qualificationService;

    private QualificationDTO qualificationDTO;
    private Qualification qualification;
    private static final String ADMIN_ROLE = "Admin";

    @BeforeEach
    void setUp() {
        qualificationDTO = new QualificationDTO();
        qualificationDTO.setId(1L);
        qualificationDTO.setQualificationName("MBBS");

        qualification = new Qualification();
        qualification.setId(1L);
        qualification.setQualificationName("MBBS");
        qualification.setStatus("Active");
    }

    // ================== saveQualification Tests ==================

    @Test
    @DisplayName("Save Qualification: Should create and save a new qualification if it does not exist")
    void saveQualification_WhenNew_ShouldSaveAndReturn() {
        // Arrange
        String newQualificationName = "MD";
        when(qualificationRepo.findQualificationByName(newQualificationName.toUpperCase())).thenReturn(null);
        // We can use thenAnswer to simulate the save operation and return an object with an ID
        when(qualificationRepo.save(any(Qualification.class))).thenAnswer(invocation -> {
            Qualification q = invocation.getArgument(0);
            q.setId(10L); // Simulate database assigning an ID
            return q;
        });

        // Act
        Qualification result = qualificationService.saveQualification(newQualificationName);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("MD", result.getQualificationName()); // Note: The service saves it in uppercase
        assertEquals("Active", result.getStatus());
        verify(qualificationRepo, times(1)).save(any(Qualification.class));
    }

    @Test
    @DisplayName("Save Qualification: Should return existing qualification if it already exists")
    void saveQualification_WhenExists_ShouldReturnExisting() {
        // Arrange
        String existingName = "MBBS";
        when(qualificationRepo.findQualificationByName(existingName.toUpperCase())).thenReturn(qualification);

        // Act
        Qualification result = qualificationService.saveQualification(existingName);

        // Assert
        assertNotNull(result);
        assertEquals(qualification.getId(), result.getId());
        // Verify that the save method was never called
        verify(qualificationRepo, never()).save(any(Qualification.class));
    }

    // ================== updateQualification Tests ==================

    @Test
    @DisplayName("Update Qualification: Should succeed for Admin when qualification is found")
    void updateQualification_Success() {
        // Arrange
        qualificationDTO.setQualificationName("md radiology"); // new name
        when(qualificationRepo.findQualificationById(qualificationDTO.getId())).thenReturn(qualification);
        when(qualificationRepo.save(any(Qualification.class))).thenReturn(qualification);

        // Act
        qualificationService.updateQualification(qualificationDTO, ADMIN_ROLE);

        // Assert
        ArgumentCaptor<Qualification> qualificationCaptor = ArgumentCaptor.forClass(Qualification.class);
        verify(qualificationRepo).save(qualificationCaptor.capture());
        Qualification capturedQualification = qualificationCaptor.getValue();

        assertEquals("MD RADIOLOGY", capturedQualification.getQualificationName()); // Verify uppercase conversion
    }

    @Test
    @DisplayName("Update Qualification: Should fail if qualification is not found")
    void updateQualification_FailsWhenNotFound() {
        // Arrange
        when(qualificationRepo.findQualificationById(anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(CustomQualificationException.class, () -> {
            qualificationService.updateQualification(qualificationDTO, ADMIN_ROLE);
        });
    }

    @Test
    @DisplayName("Update Qualification: Should fail for non-Admin role")
    void updateQualification_FailsForNonAdmin() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            qualificationService.updateQualification(qualificationDTO, "Patient");
        });
    }

    // ================== getAllQualifications Tests ==================

    @ParameterizedTest
    @ValueSource(strings = {"Admin", "Doctor", "Patient"})
    @DisplayName("Get All Qualifications: Should succeed for authorized roles")
    void getAllQualifications_SuccessForAuthorizedRoles(String userType) {
        // Arrange
        List<Qualification> qualificationList = Collections.singletonList(qualification);
        List<QualificationDTO> qualificationDTOList = Collections.singletonList(qualificationDTO);

        when(qualificationRepo.getAllActiveQualifications()).thenReturn(qualificationList);

        // FIX: Changed any(TypeToken.class) to any(Type.class)
        when(modelMapper.map(any(), any(Type.class))).thenReturn(qualificationDTOList);

        // Act
        List<QualificationDTO> result = qualificationService.getAllQualifications(userType);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(qualificationRepo, times(1)).getAllActiveQualifications();
    }

    @Test
    @DisplayName("Get All Qualifications: Should fail for unauthorized role")
    void getAllQualifications_FailsForUnauthorizedRole() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            qualificationService.getAllQualifications("Receptionist");
        });
    }
}
