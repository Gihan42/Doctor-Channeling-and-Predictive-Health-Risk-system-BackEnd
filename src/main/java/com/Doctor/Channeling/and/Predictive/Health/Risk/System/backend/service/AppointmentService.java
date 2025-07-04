package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AppointmentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Appointment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsForDashBoardProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentProjection;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(AppointmentDTO appointmentDTO, String type);
    String getOnGoingChannelingNumber(long patientId,String type);
    List<AppointmentProjection> findAllAppointmentDetailsByPatientId( long patientId,String type);
    Appointment deleteAppointment(long appointmentId, String type);
    List<AppointmentDetailsForDashBoardProjection> findAppointmentDetailsByPatientId(long patientId,String type);
}
