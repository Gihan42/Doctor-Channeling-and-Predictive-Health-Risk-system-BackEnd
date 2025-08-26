package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenterType;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterTypeRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.MedicalCenterTypeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalCenterTypeServiceImplTest {

    @Mock
    private MedicalCenterTypeRepo medicalCenterTypeRepo;

    @InjectMocks
    private MedicalCenterTypeServiceImpl medicalCenterTypeService;

    // ================== addMedicalCenterType Tests ==================

    @Test
    @DisplayName("Should save a new medical center type and return its ID when it does not exist")
    void addMedicalCenterType_ShouldSaveNewType_WhenTypeIsNew() {
        // Arrange
        String newTypeName = "Hospital";
        MedicalCenterType newTypeToSave = new MedicalCenterType(0L, newTypeName, "Active");
        MedicalCenterType savedTypeWithId = new MedicalCenterType(1L, newTypeName, "Active");

        // 1. When the repo looks for "Hospital", it finds nothing (returns null).
        when(medicalCenterTypeRepo.findByCenterType(newTypeName)).thenReturn(null);

        // 2. When the repo saves any MedicalCenterType object, return the one with an ID.
        when(medicalCenterTypeRepo.save(any(MedicalCenterType.class))).thenReturn(savedTypeWithId);

        // Act
        long returnedId = medicalCenterTypeService.addMedicalCenterType(newTypeName);

        // Assert
        // 3. The returned ID should be the one from the saved object.
        assertEquals(1L, returnedId);

        // 4. Verify that the save method was called exactly once.
        verify(medicalCenterTypeRepo, times(1)).save(any(MedicalCenterType.class));
    }

    @Test
    @DisplayName("Should return the existing ID when the medical center type already exists")
    void addMedicalCenterType_ShouldReturnExistingId_WhenTypeExists() {
        // Arrange
        String existingTypeName = "Clinic";
        MedicalCenterType existingType = new MedicalCenterType(55L, existingTypeName, "Active");

        // 1. When the repo looks for "Clinic", it finds the existing object.
        when(medicalCenterTypeRepo.findByCenterType(existingTypeName)).thenReturn(existingType);

        // Act
        long returnedId = medicalCenterTypeService.addMedicalCenterType(existingTypeName);

        // Assert
        // 2. The returned ID should be from the existing object.
        assertEquals(55L, returnedId);

        // 3. Verify that the save method was NEVER called.
        verify(medicalCenterTypeRepo, never()).save(any(MedicalCenterType.class));
    }
}