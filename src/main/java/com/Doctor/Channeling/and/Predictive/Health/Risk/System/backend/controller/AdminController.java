package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AuthenticationRequestDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AdminService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping(path = "/save")
    public ResponseEntity<StandardResponse> saveAdmin(@RequestBody AdminDTO dto, @RequestAttribute String type){
        Admin admin = adminService.saveAdmin(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", admin),
                HttpStatus.CREATED
        );
    }

    @PutMapping(path = "/update")
    public ResponseEntity<StandardResponse> updateAdmin(@RequestBody AdminDTO dto, @RequestAttribute String type){
        Admin admin = adminService.updateAdmin(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"updated", admin),
                HttpStatus.CREATED
        );
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<StandardResponse> getAllActiveAdmins( @RequestAttribute String type){
        List<AdminDTO> allActiveAdmins = adminService.getAllActiveAdmins(type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded", allActiveAdmins),
                HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"id"})
    public ResponseEntity<StandardResponse> deleteAdmin( @RequestParam long id,@RequestAttribute String type){
        Admin admin = adminService.deleteAdmin(id, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"deleted", admin),
                HttpStatus.OK
        );
    }

    @GetMapping(params = {"email"})
    public ResponseEntity<StandardResponse> getAdminByEmail( @RequestParam String email,@RequestAttribute String type){
        Admin adminByEmail = adminService.getAdminByEmail(email, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"loaded", adminByEmail),
                HttpStatus.OK
        );
    }

  @PutMapping(path = "/updatePw")
  public ResponseEntity<StandardResponse> updateAdminPassword(@RequestBody UserPasswordDTO dto, @RequestAttribute String type){
      String message = adminService.updateUserPassword(dto, type);
      return new ResponseEntity<>(
              new StandardResponse(200,"updated",message ),
              HttpStatus.OK
      );
  }

}
