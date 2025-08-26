package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorMedicalCenterRoomScheduleDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorScheduleProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomDoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorMedicalCenterRoomScheduleRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.DoctorMedicalCenterRoomScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorMedicalCenterRoomScheduleServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DoctorMedicalCenterRoomScheduleRepo doctorMedicalCenterRoomScheduleRepo;

    @InjectMocks
    private DoctorMedicalCenterRoomScheduleServiceImpl scheduleService;

    private DoctorMedicalCenterRoomScheduleDTO scheduleDTO;
    private DoctorMedicalCenterRoomSchedule schedule;
    private static final String ADMIN_ROLE = "Admin";
    private static final String PATIENT_ROLE = "Patient";
    private static final String OTHER_ROLE = "Doctor";

    @BeforeEach
    void setUp() {
        scheduleDTO = new DoctorMedicalCenterRoomScheduleDTO();
        scheduleDTO.setId(1L);
        scheduleDTO.setDoctorId(10L);

        schedule = new DoctorMedicalCenterRoomSchedule();
        schedule.setId(1L);
        schedule.setDoctorId(10L);
        schedule.setStatus("Active");
    }

    // ================== createDoctorChannelingRoomSchedule Tests ==================

    @Test
    @DisplayName("Create Schedule: Should succeed when schedule does not exist")
    void createDoctorChannelingRoomSchedule_Success() {
        // Arrange
        when(doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(scheduleDTO.getDoctorId())).thenReturn(null);
        when(modelMapper.map(scheduleDTO, DoctorMedicalCenterRoomSchedule.class)).thenReturn(schedule);
        when(doctorMedicalCenterRoomScheduleRepo.save(schedule)).thenReturn(schedule);

        // Act
        DoctorMedicalCenterRoomSchedule result = scheduleService.createDoctorChannelingRoomSchedule(scheduleDTO, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        assertEquals("Active", result.getStatus());
        verify(doctorMedicalCenterRoomScheduleRepo, times(1)).save(schedule);
    }

    @Test
    @DisplayName("Create Schedule: Should fail when schedule already exists")
    void createDoctorChannelingRoomSchedule_FailsWhenExists() {
        // Arrange
        when(doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(scheduleDTO.getDoctorId())).thenReturn(schedule);

        // Act & Assert
        assertThrows(CustomDoctorMedicalCenterRoomSchedule.class, () -> {
            scheduleService.createDoctorChannelingRoomSchedule(scheduleDTO, ADMIN_ROLE);
        });
        verify(doctorMedicalCenterRoomScheduleRepo, never()).save(any());
    }

    @Test
    @DisplayName("Create Schedule: Should fail for non-admin role")
    void createDoctorChannelingRoomSchedule_FailsForNonAdmin() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            scheduleService.createDoctorChannelingRoomSchedule(scheduleDTO, PATIENT_ROLE);
        });
    }

    // ================== updateDoctorMedicalCenterRoomSchedule Tests ==================

    @Test
    @DisplayName("Update Schedule: Should succeed when schedule exists")
    void updateDoctorMedicalCenterRoomSchedule_Success() {
        // Arrange
        when(doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(scheduleDTO.getId())).thenReturn(schedule);
        when(modelMapper.map(scheduleDTO, DoctorMedicalCenterRoomSchedule.class)).thenReturn(schedule);
        when(doctorMedicalCenterRoomScheduleRepo.save(schedule)).thenReturn(schedule);

        // Act
        DoctorMedicalCenterRoomSchedule result = scheduleService.updateDoctorMedicalCenterRoomSchedule(scheduleDTO, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        verify(doctorMedicalCenterRoomScheduleRepo, times(1)).save(schedule);
    }

    @Test
    @DisplayName("Update Schedule: Should fail when schedule not found")
    void updateDoctorMedicalCenterRoomSchedule_FailsWhenNotFound() {
        // Arrange
        when(doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(scheduleDTO.getId())).thenReturn(null);

        // Act & Assert
        assertThrows(CustomDoctorMedicalCenterRoomSchedule.class, () -> {
            scheduleService.updateDoctorMedicalCenterRoomSchedule(scheduleDTO, ADMIN_ROLE);
        });
    }

    // ================== deleteDoctorScheduleWithRoomDetails Tests ==================

    @Test
    @DisplayName("Delete Schedule: Should succeed by setting status to InActive")
    void deleteDoctorScheduleWithRoomDetails_Success() {
        // Arrange
        when(doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(anyLong())).thenReturn(schedule);
        when(doctorMedicalCenterRoomScheduleRepo.save(schedule)).thenReturn(schedule);

        // Act
        DoctorMedicalCenterRoomSchedule result = scheduleService.deleteDoctorScheduleWithRoomDetails(1L, ADMIN_ROLE);

        // Assert
        assertNotNull(result);
        assertEquals("InActive", result.getStatus());
        verify(doctorMedicalCenterRoomScheduleRepo, times(1)).save(schedule);
    }

    @Test
    @DisplayName("Delete Schedule: Should fail when schedule not found")
    void deleteDoctorScheduleWithRoomDetails_FailsWhenNotFound() {
        // Arrange
        when(doctorMedicalCenterRoomScheduleRepo.findByIdAndStatusActive(anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(CustomDoctorMedicalCenterRoomSchedule.class, () -> {
            scheduleService.deleteDoctorScheduleWithRoomDetails(1L, ADMIN_ROLE);
        });
    }

    // ================== getDoctorScheduleWithRoomDetails Tests ==================

    @ParameterizedTest
    @ValueSource(strings = {ADMIN_ROLE, PATIENT_ROLE})
    @DisplayName("Get Schedule: Should succeed for authorized roles (Admin, Patient)")
    void getDoctorScheduleWithRoomDetails_SuccessForAuthorized(String userType) {
        // Arrange
        long doctorId = 10L;
        List<DoctorScheduleProjection> mockList = Collections.singletonList(mock(DoctorScheduleProjection.class));
        when(doctorMedicalCenterRoomScheduleRepo.getDoctorScheduleWithRoomDetails(doctorId)).thenReturn(mockList);

        // Act
        List<DoctorScheduleProjection> result = scheduleService.getDoctorScheduleWithRoomDetails(doctorId, userType);

        // Assert
        assertNotNull(result);
        assertEquals(mockList, result);
        verify(doctorMedicalCenterRoomScheduleRepo, times(1)).getDoctorScheduleWithRoomDetails(doctorId);
    }

    @Test
    @DisplayName("Get Schedule: Should fail for unauthorized role")
    void getDoctorScheduleWithRoomDetails_FailsForUnauthorized() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            scheduleService.getDoctorScheduleWithRoomDetails(10L, OTHER_ROLE);
        });
        verify(doctorMedicalCenterRoomScheduleRepo, never()).getDoctorScheduleWithRoomDetails(anyLong());
    }

    // ================== findDayOfWeekByDoctorIdAndMedicalCenterId Tests ==================

    @Test
    @DisplayName("Find DayOfWeek: Should succeed for Patient role")
    void findDayOfWeekByDoctorIdAndMedicalCenterId_Success() {
        // Arrange
        List<String> days = Collections.singletonList("Monday");
        when(doctorMedicalCenterRoomScheduleRepo.findDayOfWeekByDoctorIdAndMedicalCenterId(anyLong(), anyLong())).thenReturn(days);

        // Act
        List<String> result = scheduleService.findDayOfWeekByDoctorIdAndMedicalCenterId(1L, 2L, PATIENT_ROLE);

        // Assert
        assertEquals(days, result);
    }

    @Test
    @DisplayName("Find DayOfWeek: Should fail for non-Patient role")
    void findDayOfWeekByDoctorIdAndMedicalCenterId_FailsForNonPatient() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            scheduleService.findDayOfWeekByDoctorIdAndMedicalCenterId(1L, 2L, ADMIN_ROLE);
        });
    }
}