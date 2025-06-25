package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AppointmentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Appointment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AppointmentService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createAppointment(@RequestBody AppointmentDTO dto,
                                                       @RequestAttribute String type){
        Appointment appointment = appointmentService.createAppointment(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", appointment),
                HttpStatus.CREATED
        );

    }

    @GetMapping(params = {"patientId"})
    public ResponseEntity<StandardResponse> getOnGoingChannelingNumber(@RequestParam("patientId") long patientId,
                                                              @RequestAttribute String type){

        String onGoingChannelingNumber =
                appointmentService.getOnGoingChannelingNumber(patientId, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", onGoingChannelingNumber),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getAllAppointmentByPatientId", params = {"patientId"})
    public ResponseEntity<StandardResponse> findAllAppointmentDetailsByPatientId(
            @RequestParam("patientId") long patientId,
            @RequestAttribute String type) {
        List<AppointmentProjection> allAppointmentDetailsByPatientId =
                appointmentService.findAllAppointmentDetailsByPatientId(patientId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success",allAppointmentDetailsByPatientId ),
                HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"appointmentId"})
    public ResponseEntity<StandardResponse> deleteAppointment(@RequestParam("appointmentId") long appointmentId,
                                                               @RequestAttribute String type) {
        Appointment appointment = appointmentService.deleteAppointment(appointmentId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "Appointment Deleted", appointment),
                HttpStatus.OK
        );
    }
}
