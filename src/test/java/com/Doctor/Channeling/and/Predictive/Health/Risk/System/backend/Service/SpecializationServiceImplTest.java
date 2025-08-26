package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SpecializationDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomSpecializationException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.SpecializationRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.SpecializationServiceImpl;
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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializationServiceImplTest {

    @Mock
    private SpecializationRepo specializationRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SpecializationServiceImpl specializationService;

    private SpecializationDTO specializationDTO;
    private Specialization specialization;
    private static final String ADMIN_ROLE = "Admin";
    private static final String PATIENT_ROLE = "Patient";

    @BeforeEach
    void setUp() {
        specializationDTO = new SpecializationDTO();
        specializationDTO.setId(1L);
        specializationDTO.setSpecializationName("Cardiology");

        specialization = new Specialization();
        specialization.setId(1L);
        specialization.setSpecializationName("Cardiology");
        specialization.setStatus("Active");
    }

    // ================== saveSpecialization Tests ==================

    @Test
    @DisplayName("Save Specialization: Should normalize name, save, and return a new specialization")
    void saveSpecialization_WhenNew_ShouldNormalizeAndSave() {
        // Arrange
        String rawInput = "cardiology";
        String normalizedName = "Cardiology";

        when(specializationRepo.findBySpecializationAndStatusActive(normalizedName)).thenReturn(null);
        when(specializationRepo.save(any(Specialization.class))).thenAnswer(invocation -> {
            Specialization specToSave = invocation.getArgument(0);
            specToSave.setId(10L); // Simulate DB assigning an ID
            return specToSave;
        });

        // Act
        Specialization result = specializationService.saveSpecialization(rawInput);

        // Assert
        ArgumentCaptor<Specialization> captor = ArgumentCaptor.forClass(Specialization.class);
        verify(specializationRepo).save(captor.capture());
        Specialization savedSpec = captor.getValue();

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(normalizedName, savedSpec.getSpecializationName()); // Verify it was saved with the normalized name
        verify(specializationRepo, times(1)).save(any(Specialization.class));
    }

    @Test
    @DisplayName("Save Specialization: Should return existing specialization without saving")
    void saveSpecialization_WhenExists_ShouldReturnExisting() {
        // Arrange
        String rawInput = "cardiology";
        String normalizedName = "Cardiology";
        when(specializationRepo.findBySpecializationAndStatusActive(normalizedName)).thenReturn(specialization);

        // Act
        Specialization result = specializationService.saveSpecialization(rawInput);

        // Assert
        assertNotNull(result);
        assertEquals(specialization.getId(), result.getId());
        assertEquals(normalizedName, result.getSpecializationName());
        verify(specializationRepo, never()).save(any(Specialization.class));
    }

    // ================== updateSpecialization Tests ==================

    @Test
    @DisplayName("Update Specialization: Should succeed for Admin when specialization is found")
    void updateSpecialization_Success() {
        // Arrange
        specializationDTO.setSpecializationName("neurology"); // New name, not normalized
        when(specializationRepo.findByIdAndStatusActive(specializationDTO.getId())).thenReturn(specialization);
        when(specializationRepo.save(any(Specialization.class))).thenReturn(specialization);

        // Act
        specializationService.updateSpecialization(specializationDTO, ADMIN_ROLE);

        // Assert
        ArgumentCaptor<Specialization> captor = ArgumentCaptor.forClass(Specialization.class);
        verify(specializationRepo).save(captor.capture());
        Specialization updatedSpec = captor.getValue();

        assertEquals("Neurology", updatedSpec.getSpecializationName()); // Verify the name was normalized before saving
    }

    @Test
    @DisplayName("Update Specialization: Should fail if specialization is not found")
    void updateSpecialization_FailsWhenNotFound() {
        // Arrange
        when(specializationRepo.findByIdAndStatusActive(anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(CustomSpecializationException.class, () -> {
            specializationService.updateSpecialization(specializationDTO, ADMIN_ROLE);
        });
    }

    // ================== getAllSpecializations Tests ==================

    @ParameterizedTest
    @ValueSource(strings = {"Admin", "Doctor", "Patient"})
    @DisplayName("Get All Specializations: Should succeed for authorized roles")
    void getAllSpecializations_SuccessForAuthorizedRoles(String userType) {
        // Arrange
        when(specializationRepo.getAllActiveSpecializations()).thenReturn(Collections.singletonList(specialization));
        when(modelMapper.map(any(), any(Type.class))).thenReturn(Collections.singletonList(specializationDTO));

        // Act
        List<SpecializationDTO> result = specializationService.getAllSpecializations(userType);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(specializationRepo, times(1)).getAllActiveSpecializations();
    }

    // ================== findMedicalCentersBySpecialization Tests ==================

    @Test
    @DisplayName("Find Medical Centers by Specialization: Should succeed for Patient")
    void findMedicalCentersBySpecialization_Success() {
        // Arrange
        when(specializationRepo.findMedicalCentersBySpecialization("Cardiology")).thenReturn(Collections.emptyList());

        // Act
        specializationService.findMedicalCentersBySpecialization("Cardiology", PATIENT_ROLE);

        // Assert
        verify(specializationRepo, times(1)).findMedicalCentersBySpecialization("Cardiology");
    }

    @Test
    @DisplayName("Find Medical Centers by Specialization: Should fail for non-Patient")
    void findMedicalCentersBySpecialization_FailsForNonPatient() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            specializationService.findMedicalCentersBySpecialization("Cardiology", ADMIN_ROLE);
        });
    }
}
