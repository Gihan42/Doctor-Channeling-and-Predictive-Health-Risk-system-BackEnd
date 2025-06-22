package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.MedicalCenterDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.ChannelingRoomService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channeling/room")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class ChannelingRoomController {

    private final ChannelingRoomService channelingRoomService;

    @GetMapping(params = {"id"})
    public ResponseEntity<StandardResponse> getAllChannelingRoomByMedicalCenterId(@RequestParam("id") long id,
                                                              @RequestAttribute String type){
        List<ChannelingRoomProjection> allChannelingRoomsByMedicalCenter =
                channelingRoomService.findAllChannelingRoomsByMedicalCenter(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", allChannelingRoomsByMedicalCenter),
                HttpStatus.OK
        );
    }
}
