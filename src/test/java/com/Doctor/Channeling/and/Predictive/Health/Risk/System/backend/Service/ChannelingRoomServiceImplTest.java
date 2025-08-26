package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.ChannelingRoomServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelingRoomServiceImplTest {

    @Mock
    private MedicalCenterRepo medicalCenterRepo;

    // ModelMapper is mocked because it's a dependency, even if unused in this method
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChannelingRoomServiceImpl channelingRoomService;

    // ================== findAllChannelingRoomsByMedicalCenter Tests ==================

    @ParameterizedTest
    @ValueSource(strings = {"Admin", "Patient"})
    @DisplayName("Should return channeling rooms for authorized roles (Admin, Patient)")
    void findAllChannelingRooms_SuccessForAuthorizedRoles(String userType) {
        // Arrange
        long medicalCenterId = 1L;
        // Create a mock projection to return
        ChannelingRoomProjection mockProjection = mock(ChannelingRoomProjection.class);
        List<ChannelingRoomProjection> expectedRooms = Collections.singletonList(mockProjection);

        when(medicalCenterRepo.getAllChannelingRoomsByMedicalCenterId(medicalCenterId)).thenReturn(expectedRooms);

        // Act
        List<ChannelingRoomProjection> actualRooms = channelingRoomService.findAllChannelingRoomsByMedicalCenter(medicalCenterId, userType);

        // Assert
        assertNotNull(actualRooms);
        assertEquals(1, actualRooms.size());
        assertEquals(expectedRooms, actualRooms);
        // Verify that the repository method was called exactly once
        verify(medicalCenterRepo, times(1)).getAllChannelingRoomsByMedicalCenterId(medicalCenterId);
    }

    @Test
    @DisplayName("Should throw CustomBadCredentialsException for unauthorized roles")
    void findAllChannelingRooms_FailsForUnauthorizedRole() {
        // Arrange
        long medicalCenterId = 1L;
        String unauthorizedUserType = "Doctor";

        // Act & Assert
        CustomBadCredentialsException exception = assertThrows(CustomBadCredentialsException.class, () -> {
            channelingRoomService.findAllChannelingRoomsByMedicalCenter(medicalCenterId, unauthorizedUserType);
        });

        assertEquals("dont have permission", exception.getMessage());
        // Verify that the repository method was NEVER called because the check failed early
        verify(medicalCenterRepo, never()).getAllChannelingRoomsByMedicalCenterId(anyLong());
    }
}
