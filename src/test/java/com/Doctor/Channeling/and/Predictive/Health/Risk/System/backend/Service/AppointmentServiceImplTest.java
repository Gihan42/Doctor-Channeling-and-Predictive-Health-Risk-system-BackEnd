package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AppointmentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Appointment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomAppointmentException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AppointmentRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorMedicalCenterRoomScheduleRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DoctorRepo doctorRepo;

    @Mock
    private DoctorMedicalCenterRoomScheduleRepo scheduleRepo;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private AppointmentDTO appointmentDTO;
    private Appointment appointment;
    private DoctorMedicalCenterRoomSchedule schedule;
    private static final String PATIENT_ROLE = "Patient";
    private static final String ADMIN_ROLE = "Admin";
    private static final String OTHER_ROLE = "Doctor";

    @BeforeEach
    void setUp() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date appointmentDate = sdf.parse("2025-09-15"); // This is a Monday

        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setDoctorId(1L);
        appointmentDTO.setPatientId(2L);
        appointmentDTO.setMedicleCenterId(3L);
        appointmentDTO.setAppointmentDate(appointmentDate);

        appointment = new Appointment();
        appointment.setId(100L);
        appointment.setDoctorId(1L);
        appointment.setPatientId(2L);
        appointment.setMedicleCenterId(3L);
        appointment.setRoomId(5L);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentStatus("Pending");

        schedule = new DoctorMedicalCenterRoomSchedule();
        schedule.setId(50L);
        schedule.setDoctorId(1L);
        schedule.setMedicalCenterId(3L);
        schedule.setDayOfWeek("Monday");
        schedule.setStartTime("09.00 AM");
        schedule.setEndTime("11.00 AM");
        schedule.setChannelingRoomId(5L);
    }

    // ================== createAppointment Tests ==================

    @Test
    @DisplayName("Should create appointment successfully when schedule is available")
    void createAppointment_Success() {
        // Arrange
        when(scheduleRepo.findByDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive(anyLong(), anyLong(), eq("Monday")))
                .thenReturn(schedule);
        when(doctorRepo.findPatientCountByDoctorId(anyLong())).thenReturn(10); // Doctor can see 10 patients
        when(appointmentRepo.countByDoctorIdAndSheduleIdAndAppointmentDate(anyLong(), anyLong(), any(Date.class)))
                .thenReturn(5); // 5 appointments already booked
        when(modelMapper.map(any(AppointmentDTO.class), eq(Appointment.class))).thenReturn(appointment);
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment createdAppointment = appointmentService.createAppointment(appointmentDTO, PATIENT_ROLE);

        // Assert
        assertNotNull(createdAppointment);
        assertEquals("Pending", createdAppointment.getAppointmentStatus());
        assertEquals(6, createdAppointment.getChannelNumber()); // 5 booked + 1
        verify(appointmentRepo, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw exception when creating appointment with non-patient role")
    void createAppointment_FailsWhenNotPatient() {
        // Act & Assert
        CustomBadCredentialsException exception = assertThrows(CustomBadCredentialsException.class, () -> {
            appointmentService.createAppointment(appointmentDTO, OTHER_ROLE);
        });
        assertEquals("You don't have permission to perform this action.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when doctor schedule is not found")
    void createAppointment_FailsWhenNoScheduleExists() {
        // Arrange
        when(scheduleRepo.findByDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive(anyLong(), anyLong(), anyString()))
                .thenReturn(null);

        // Act & Assert
        CustomAppointmentException exception = assertThrows(CustomAppointmentException.class, () -> {
            appointmentService.createAppointment(appointmentDTO, PATIENT_ROLE);
        });
        assertEquals("The doctor does not have a schedule for the selected day.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when doctor's schedule is full")
    void createAppointment_FailsWhenScheduleIsFull() {
        // Arrange
        when(scheduleRepo.findByDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive(anyLong(), anyLong(), anyString()))
                .thenReturn(schedule);
        when(doctorRepo.findPatientCountByDoctorId(anyLong())).thenReturn(10);
        when(appointmentRepo.countByDoctorIdAndSheduleIdAndAppointmentDate(anyLong(), anyLong(), any(Date.class)))
                .thenReturn(10); // Schedule is full

        // Act & Assert
        CustomAppointmentException exception = assertThrows(CustomAppointmentException.class, () -> {
            appointmentService.createAppointment(appointmentDTO, PATIENT_ROLE);
        });
        assertEquals("The doctor's schedule is full for this day. Please choose another date.", exception.getMessage());
    }

    // ================== deleteAppointment Tests ==================

    @Test
    @DisplayName("Should delete appointment successfully")
    void deleteAppointment_Success() {
        // Arrange
        when(appointmentRepo.findByIdAndStatus(anyLong())).thenReturn(appointment);
        doNothing().when(appointmentRepo).delete(any(Appointment.class));

        // Act
        Appointment deletedAppointment = appointmentService.deleteAppointment(100L, PATIENT_ROLE);

        // Assert
        assertNotNull(deletedAppointment);
        assertEquals(appointment.getId(), deletedAppointment.getId());
        verify(appointmentRepo, times(1)).delete(appointment);
    }

    @Test
    @DisplayName("Should throw exception when deleting a non-existent appointment")
    void deleteAppointment_FailsWhenAppointmentNotFound() {
        // Arrange
        when(appointmentRepo.findByIdAndStatus(anyLong())).thenReturn(null);

        // Act & Assert
        CustomAppointmentException exception = assertThrows(CustomAppointmentException.class, () -> {
            appointmentService.deleteAppointment(100L, PATIENT_ROLE);
        });
        assertEquals("Appointment does not exist", exception.getMessage());
    }

    // ================== getOnGoingChannelingNumber Tests ==================

    @Test
    @DisplayName("Should return channel number for an ongoing appointment")
    void getOnGoingChannelingNumber_Success() {
        // Arrange
        when(appointmentRepo.findByPatientId(anyLong())).thenReturn(Collections.singletonList(appointment));
        when(appointmentRepo.getCurrentChannelNumberByMedicineCenterIdAndRoomId(any(Date.class), anyLong(), anyLong()))
                .thenReturn("5");

        // Act
        String channelNumber = appointmentService.getOnGoingChannelingNumber(2L, PATIENT_ROLE);

        // Assert
        assertEquals("5", channelNumber);
    }

    @Test
    @DisplayName("Should return 'closed' message when no ongoing appointment found")
    void getOnGoingChannelingNumber_ReturnsClosedMessage() {
        // Arrange
        when(appointmentRepo.findByPatientId(anyLong())).thenReturn(Collections.singletonList(appointment));
        when(appointmentRepo.getCurrentChannelNumberByMedicineCenterIdAndRoomId(any(Date.class), anyLong(), anyLong()))
                .thenReturn(null);

        // Act
        String message = appointmentService.getOnGoingChannelingNumber(2L, PATIENT_ROLE);

        // Assert
        assertEquals("Your appointment is closed", message);
    }

    // ================== Admin Access Tests ==================

    @Test
    @DisplayName("Admin should be able to count pending appointments")
    void countByActiveDoctors_SuccessForAdmin() {
        // Arrange
        when(appointmentRepo.countPendingAppointments()).thenReturn(25);

        // Act
        int count = appointmentService.countByActiveDoctors(ADMIN_ROLE);

        // Assert
        assertEquals(25, count);
    }

    @Test
    @DisplayName("Non-admin should not be able to count pending appointments")
    void countByActiveDoctors_FailsForNonAdmin() {
        // Act & Assert
        assertThrows(CustomBadCredentialsException.class, () -> {
            appointmentService.countByActiveDoctors(PATIENT_ROLE);
        });
    }

    // ================== Helper Method Tests ==================

    @Test
    @DisplayName("Should generate time slots correctly")
    void generateTimeSlots_Success() {
        // Arrange
        String startTime = "9.00 AM";
        String endTime = "11.00 AM"; // 2 hours = 120 minutes
        int patientCount = 4;        // 120 / 4 = 30 minutes per slot

        // Act
        List<String[]> slots = appointmentService.generateTimeSlots(startTime, endTime, patientCount);

        // Assert
        assertNotNull(slots);
        assertEquals(4, slots.size());
        assertEquals("09:00", slots.get(0)[0]); // First slot start time
        assertEquals("09:30", slots.get(0)[1]); // First slot end time
        assertEquals("10:30", slots.get(3)[0]); // Last slot start time
        assertEquals("11:00", slots.get(3)[1]); // Last slot end time
    }
}