package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientReviewDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.PatientReview;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomPatientReviewException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PatientReviewRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.PatientReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientReviewServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PatientReviewRepo patientReviewRepo;

    @InjectMocks
    private PatientReviewServiceImpl patientReviewService;

    private PatientReviewDTO patientReviewDTO;
    private PatientReview patientReview;
    private static final String ADMIN_ROLE = "Admin";
    private static final String PATIENT_ROLE = "Patient";

    @BeforeEach
    void setUp() {
        patientReviewDTO = new PatientReviewDTO();
        patientReviewDTO.setPatientReviewId(1L);
        patientReviewDTO.setComment("Excellent service");

        patientReview = new PatientReview();
        patientReview.setPatientReviewId(1L);
        patientReview.setComment("Excellent service");
    }

    // ================== createPatientReview Tests ==================

    @Test
    @DisplayName("Create Review: Should succeed for a new review by a Patient")
    void createPatientReview_Success() {
        // Arrange
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(null);
        when(modelMapper.map(any(PatientReviewDTO.class), eq(PatientReview.class))).thenReturn(patientReview);
        when(patientReviewRepo.save(any(PatientReview.class))).thenReturn(patientReview);

        // Act
        PatientReview result = patientReviewService.createPatientReview(patientReviewDTO, PATIENT_ROLE);

        // Assert
        assertNotNull(result);
        assertEquals("Active", result.getStatus());
        verify(patientReviewRepo, times(1)).save(patientReview);
    }

    @Test
    @DisplayName("Create Review: Should fail if review ID already exists")
    void createPatientReview_FailsWhenExists() {
        // Arrange
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(patientReview);

        // Act & Assert
        assertThrows(CustomPatientReviewException.class, () -> {
            patientReviewService.createPatientReview(patientReviewDTO, PATIENT_ROLE);
        });
    }

    @Test
    @DisplayName("Create Review: Should fail for non-Patient role")
    void createPatientReview_FailsForNonPatient() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            patientReviewService.createPatientReview(patientReviewDTO, ADMIN_ROLE);
        });
    }

    // ================== activePatientReview (Admin action) Tests ==================

    @Test
    @DisplayName("Activate Review: Should succeed for Admin when view count is below limit")
    void activePatientReview_Success() {
        // Arrange
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(patientReview);
        when(patientReviewRepo.countViewedPatientReview()).thenReturn(2); // Below or at the limit
        when(patientReviewRepo.save(any(PatientReview.class))).thenReturn(patientReview);

        // Act
        PatientReview result = patientReviewService.activePatientReview(1L, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        assertTrue(result.isViewed());
        verify(patientReviewRepo, times(1)).save(patientReview);
    }

    @Test
    @DisplayName("Activate Review: Should fail for Admin when view count exceeds limit")
    void activePatientReview_FailsWhenLimitExceeded() {
        // Arrange
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(patientReview);
        when(patientReviewRepo.countViewedPatientReview()).thenReturn(3); // Over the limit

        // Act & Assert
        assertThrows(CustomPatientReviewException.class, () -> {
            patientReviewService.activePatientReview(1L, ADMIN_ROLE);
        });
    }

    // ================== inActivePatientReview Tests ==================

    @Test
    @DisplayName("Inactivate Review: Should succeed if review was previously viewed")
    void inActivePatientReview_Success() {
        // Arrange
        patientReview.setViewed(true); // Pre-condition: review is viewed
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(patientReview);
        when(patientReviewRepo.save(any(PatientReview.class))).thenReturn(patientReview);

        // Act
        PatientReview result = patientReviewService.inActivePatientReview(1L, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        assertFalse(result.isViewed());
        verify(patientReviewRepo, times(1)).save(patientReview);
    }

    @Test
    @DisplayName("Inactivate Review: Should fail if review was not previously viewed")
    void inActivePatientReview_FailsIfNotViewed() {
        // Arrange
        patientReview.setViewed(false); // Pre-condition: review is NOT viewed
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(patientReview);

        // Act & Assert
        assertThrows(CustomPatientReviewException.class, () -> {
            patientReviewService.inActivePatientReview(1L, ADMIN_ROLE);
        });
    }

    // ================== deletePatientReview Tests ==================

    @Test
    @DisplayName("Delete Review: Should succeed by setting status to Inactive")
    void deletePatientReview_Success() {
        // Arrange
        when(patientReviewRepo.findByPatientReviewId(anyLong())).thenReturn(patientReview);
        when(patientReviewRepo.save(any(PatientReview.class))).thenReturn(patientReview);

        // Act
        PatientReview result = patientReviewService.deletePatientReview(1L, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        assertEquals("Inactive", result.getStatus());
        verify(patientReviewRepo, times(1)).save(patientReview);
    }

    // ================== getAllPatientReviews Test (No Auth) ==================

    @Test
    @DisplayName("Get All Reviews: Should succeed as auth is commented out")
    void getAllPatientReviews_Success() {
        // Arrange
        when(patientReviewRepo.getAllPatientReviews()).thenReturn(null); // Return dummy value

        // Act
        patientReviewService.getAllPatientReviews();

        // Assert
        verify(patientReviewRepo, times(1)).getAllPatientReviews();
    }
}