package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.DoctorDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.MedicalCenterDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterWithTypeProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCentersAndIds;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical/center")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class MedicalCenterController {

    private final MedicalCenterService medicalCenterService;

    @PostMapping(path = "/save")
    public ResponseEntity<StandardResponse> saveMedicalCenter(@RequestBody MedicalCenterDTO dto,
                                                       @RequestAttribute String type){
        System.out.println(dto);
        MedicalCenter medicalCenter = medicalCenterService.createMedicalCenter(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", medicalCenter),
                HttpStatus.CREATED
        );
    }

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updateMedicalCenter(@RequestBody MedicalCenterDTO dto,
                                                       @RequestAttribute String type){
        MedicalCenter medicalCenter = medicalCenterService.updateMedicalCenter(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"updated", medicalCenter),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"id"})
    public ResponseEntity<StandardResponse> getMedicalCenterByCity(@RequestParam long id,
                                                                    @RequestAttribute String type) {
        List<MedicalCentersAndIds> medicalCenterByCity = medicalCenterService.getMedicalCenterByCity(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", medicalCenterByCity),
                HttpStatus.OK
        );
    }
    
    @GetMapping(path = "/getAll")
    public ResponseEntity<StandardResponse> getAllActiveMedicalCenters(@RequestAttribute String type) {
        List<MedicalCenterWithTypeProjection> allActiveMedicalCenters = medicalCenterService.getAllActiveMedicalCenters(type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", allActiveMedicalCenters),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"registrationNo"})
    public ResponseEntity<StandardResponse> getMedicalCenterByRegistrationId(@RequestParam String registrationNo,
                                                                              @RequestAttribute String type) {
        MedicalCenterWithTypeProjection medicalCenter = medicalCenterService.getMedicalCenterByRegistrationId(registrationNo, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", medicalCenter),
                HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"id"})
    public ResponseEntity<StandardResponse> deleteMedicalCenter(@RequestParam("id") long id,
                                                                 @RequestAttribute String type) {
        MedicalCenter deletedMedicalCenter = medicalCenterService.deleteMedicalCenter(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "deleted", deletedMedicalCenter),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/channelingRooms", params = {"id"})
    public ResponseEntity<StandardResponse> getAllChannelingRoomsByMedicalCenterId(@RequestParam long id,
                                                                                   @RequestAttribute String type) {
        List<ChannelingRoomProjection> allChannelingRoomsByMedicalCenterId =
                medicalCenterService.getAllChannelingRoomsByMedicalCenterId(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", allChannelingRoomsByMedicalCenterId),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getAllMedicalCentersAndIds")
    public ResponseEntity<StandardResponse> getAllMedicalCentersAndIds(@RequestAttribute String type) {
        List<MedicalCentersAndIds> allMedicalCentersAndIds = medicalCenterService.getAllMedicalCentersAndIds(type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", allMedicalCentersAndIds),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getById", params = {"id"})
    public ResponseEntity<StandardResponse> findMedicalCenterById(@RequestParam long id,
                                                                   @RequestAttribute String type) {
        MedicalCenter medicalCenter = medicalCenterService.findMedicalCenterById(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", medicalCenter),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/count")
    public ResponseEntity<StandardResponse> getCountOfMedicalCenters(@RequestAttribute String type) {
        int count = medicalCenterService.getCountOfMedicalCenters(type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", count),
                HttpStatus.OK
        );
    }

}
