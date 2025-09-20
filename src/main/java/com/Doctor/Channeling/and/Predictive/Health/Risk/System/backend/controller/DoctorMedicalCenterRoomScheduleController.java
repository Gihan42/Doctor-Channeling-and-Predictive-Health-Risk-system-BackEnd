package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorMedicalCenterRoomScheduleDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorScheduleProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterWithTypeProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ScheduleProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.DoctorMedicalCenterRoomScheduleService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channeling/room/schedule")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class DoctorMedicalCenterRoomScheduleController {

    private final DoctorMedicalCenterRoomScheduleService doctorMedicalCenterRoomScheduleService;

    @PostMapping(path = "/save")
    public ResponseEntity<StandardResponse> createDoctorChannelingRoomSchedule(@RequestBody DoctorMedicalCenterRoomScheduleDTO dto,
                                                                             @RequestAttribute String type) {
        DoctorMedicalCenterRoomSchedule doctorChannelingRoomSchedule = doctorMedicalCenterRoomScheduleService.createDoctorChannelingRoomSchedule(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", doctorChannelingRoomSchedule),
                HttpStatus.CREATED
        );
    }

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updateDoctorMedicalCenterRoomSchedule(@RequestBody DoctorMedicalCenterRoomScheduleDTO dto,
                                                                                  @RequestAttribute String type) {
        DoctorMedicalCenterRoomSchedule updatedSchedule = doctorMedicalCenterRoomScheduleService.updateDoctorMedicalCenterRoomSchedule(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "updated", updatedSchedule),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/doctor-schedule",params = {"doctorId"})
    public ResponseEntity<StandardResponse> getDoctorScheduleWithRoomDetails(@RequestParam("doctorId") long doctorId,
                                                                              @RequestAttribute String type) {
        List<DoctorScheduleProjection> doctorScheduleWithRoomDetails =
                doctorMedicalCenterRoomScheduleService.getDoctorScheduleWithRoomDetails(doctorId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", doctorScheduleWithRoomDetails),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/medical-center-schedule",params = {"medicalCenterId"})
    public ResponseEntity<StandardResponse> getDoctorScheduleWithRoomDetailsByMedicalCenterId(@RequestParam("medicalCenterId") long medicalCenterId,
                                                                                                  @RequestAttribute String type) {
        List<DoctorScheduleProjection> doctorScheduleWithRoomDetails =
                doctorMedicalCenterRoomScheduleService.getDoctorScheduleWithRoomDetailsByMedicalCenterId(medicalCenterId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", doctorScheduleWithRoomDetails),
                HttpStatus.OK
        );
    }

    @DeleteMapping(path = "/delete",params = {"id"})
    public ResponseEntity<StandardResponse> deleteDoctorScheduleWithRoomDetails(@RequestParam("id") long id,
                                                                                 @RequestAttribute String type) {
        DoctorMedicalCenterRoomSchedule deletedSchedule = doctorMedicalCenterRoomScheduleService.deleteDoctorScheduleWithRoomDetails(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "deleted", deletedSchedule),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"doctorId","medcleCenterId"})
    public ResponseEntity<StandardResponse> findDayOfWeekByDoctorIdAndMedicalCenterId(@RequestParam("doctorId") long doctorId,
                                                                                @RequestParam("medcleCenterId") long medcleCenterId,
                                                                                @RequestAttribute String type) {
        List<String> dayOfWeekByDoctorIdAndMedicalCenterId =
                doctorMedicalCenterRoomScheduleService.findDayOfWeekByDoctorIdAndMedicalCenterId(doctorId, medcleCenterId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", dayOfWeekByDoctorIdAndMedicalCenterId),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"doctorId","dayOfWeek"})
    public ResponseEntity<StandardResponse> findStartTimeByDoctorId(@RequestParam("doctorId") long doctorId,
                                                                     @RequestParam("dayOfWeek") String dayOfWeek,
                                                                     @RequestAttribute String type) {
        List<String> startTimeByDoctorId = doctorMedicalCenterRoomScheduleService.findStartTimeByDoctorId(doctorId, dayOfWeek,type);
        System.out.println(startTimeByDoctorId);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", startTimeByDoctorId),
                HttpStatus.OK
        );
    }
    @GetMapping(path = "/all-active-schedules")
    public ResponseEntity<StandardResponse> findAllActiveDoctorSchedules(@RequestAttribute String type) {
        List<ScheduleProjection> allActiveDoctorSchedules =
                doctorMedicalCenterRoomScheduleService.findAllActiveDoctorSchedules(type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", allActiveDoctorSchedules),
                HttpStatus.OK
        );
    }
}
