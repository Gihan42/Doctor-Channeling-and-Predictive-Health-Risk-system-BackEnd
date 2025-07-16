package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;

import java.util.List;

public interface AdminService {

    Admin saveAdmin(AdminDTO adminDTO,String type);
    String generateAdminId();
    Admin updateAdmin(AdminDTO adminDTO,String type);
    List<AdminDTO> getAllActiveAdmins(String type);
    Admin deleteAdmin(long id, String type);
    Admin getAdminByEmail(String email,String type);
    String updateUserPassword(UserPasswordDTO dto, String type);
    Admin getAdminById(long id, String type);

}
