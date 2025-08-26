package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.MedicalCenterDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomMedicalCenterException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PatientRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterTypeService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.MedicalCenterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalCenterServiceImplTest {
    private static final Logger log = LoggerFactory.getLogger(MedicalCenterServiceImpl.class);

    @Mock
    private MedicalCenterRepo medicalCenterRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private MedicalCenterTypeService medicalCenterTypeService;
    @Mock
    private PatientRepo patientRepo;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MedicalCenterServiceImpl medicalCenterService;

    private MedicalCenterDTO medicalCenterDTO;
    private MedicalCenter medicalCenter;
    private Patient patient;
    private static final String ADMIN_ROLE = "Admin";
    private static final String PATIENT_ROLE = "Patient";

    @BeforeEach
    void setUp() {
        medicalCenterDTO = new MedicalCenterDTO();
        medicalCenterDTO.setId(1L);
        medicalCenterDTO.setRegistrationNumber("MC-123");
        medicalCenterDTO.setMedicalCenterType("Hospital");
        // Use a format that matches the service implementation
        medicalCenterDTO.setOpenTime("2025-01-01T09:00:00");
        medicalCenterDTO.setCloseTime("2025-01-01T17:00:00");

        medicalCenter = new MedicalCenter();
        medicalCenter.setId(1L);

        patient = new Patient();
        patient.setCity("Colombo");
    }

    // ================== createMedicalCenter Tests ==================

    @Test
    @DisplayName("Create Medical Center: Should succeed for a new center")
    void createMedicalCenter_Success() {
        // Arrange
        when(medicalCenterRepo.findMedicalCenterById(anyLong())).thenReturn(null);
        when(medicalCenterRepo.findMedicalCenterByRegistrationNumber(anyString())).thenReturn(null);
        when(medicalCenterTypeService.addMedicalCenterType(anyString())).thenReturn(1L);
        when(modelMapper.map(any(MedicalCenterDTO.class), eq(MedicalCenter.class))).thenReturn(medicalCenter);
        when(medicalCenterRepo.save(any(MedicalCenter.class))).thenReturn(medicalCenter);

        // Act
        MedicalCenter result = medicalCenterService.createMedicalCenter(medicalCenterDTO, ADMIN_ROLE);

        // Assert
        ArgumentCaptor<MedicalCenter> captor = ArgumentCaptor.forClass(MedicalCenter.class);
        verify(medicalCenterRepo).save(captor.capture());
        MedicalCenter capturedCenter = captor.getValue();

        assertNotNull(result);
        assertEquals("Active", capturedCenter.getStatus());
        assertNotNull(capturedCenter.getOpenTime()); // Check that time was parsed and set
        verify(medicalCenterTypeService, times(1)).addMedicalCenterType("Hospital");
    }

    @Test
    @DisplayName("Create Medical Center: Should fail if center already exists")
    void createMedicalCenter_FailsWhenExists() {
        // Arrange
        when(medicalCenterRepo.findMedicalCenterById(anyLong())).thenReturn(medicalCenter);

        // Act & Assert
        assertThrows(CustomMedicalCenterException.class, () -> {
            medicalCenterService.createMedicalCenter(medicalCenterDTO, ADMIN_ROLE);
        });
    }

    // ================== getMedicalCenterByCity Tests (External API) ==================

    @Test
    @DisplayName("Get by City: Should return medical centers on successful API call")
    void getMedicalCenterByCity_Success() {
        // Arrange
        String nominatimJsonResponse = "[{\"address\": {\"state_district\": \"Colombo District\"}}]";
        ResponseEntity<String> apiResponse = new ResponseEntity<>(nominatimJsonResponse, HttpStatus.OK);

        when(patientRepo.findByPatientId(1L)).thenReturn(patient);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(apiResponse);
        when(medicalCenterRepo.getMedicalCenterByDistrict("Colombo")).thenReturn(Collections.singletonList(null)); // Return dummy list

        // Act
        List<?> result = medicalCenterService.getMedicalCenterByCity(1L, PATIENT_ROLE);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class));
        verify(medicalCenterRepo, times(1)).getMedicalCenterByDistrict("Colombo");
    }


    @Test
    @DisplayName("Get by City: Should fail if patient not found")
    void getMedicalCenterByCity_FailsWhenPatientNotFound() {
        // Arrange
        when(patientRepo.findByPatientId(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomMedicalCenterException.class, () -> {
            medicalCenterService.getMedicalCenterByCity(1L, PATIENT_ROLE);
        });
    }

    // ================== deleteMedicalCenter Tests ==================

    @Test
    @DisplayName("Delete Medical Center: Should succeed by setting status to Inactive")
    void deleteMedicalCenter_Success() {
        // Arrange
        when(medicalCenterRepo.findMedicalCenterById(1L)).thenReturn(medicalCenter);
        when(medicalCenterRepo.save(any(MedicalCenter.class))).thenReturn(medicalCenter);

        // Act
        MedicalCenter result = medicalCenterService.deleteMedicalCenter(1L, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        assertEquals("Inactive", result.getStatus());
        verify(medicalCenterRepo, times(1)).save(medicalCenter);
    }

    @Test
    @DisplayName("Delete Medical Center: Should fail if center not found or already inactive")
    void deleteMedicalCenter_FailsWhenNotFound() {
        // Arrange
        when(medicalCenterRepo.findMedicalCenterById(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomMedicalCenterException.class, () -> {
            medicalCenterService.deleteMedicalCenter(1L, ADMIN_ROLE);
        });
    }

    // ================== getCountOfMedicalCenters Tests ==================

    @Test
    @DisplayName("Get Count: Should return active center count for Admin")
    void getCountOfMedicalCenters_SuccessForAdmin() {
        // Arrange
        when(medicalCenterRepo.getActiveMedicalCenterCount()).thenReturn(15);

        // Act
        int count = medicalCenterService.getCountOfMedicalCenters(ADMIN_ROLE);

        // Assert
        assertEquals(15, count);
    }

    @Test
    @DisplayName("Get Count: Should fail for non-Admin role")
    void getCountOfMedicalCenters_FailsForNonAdmin() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            medicalCenterService.getCountOfMedicalCenters(PATIENT_ROLE);
        });
    }
}