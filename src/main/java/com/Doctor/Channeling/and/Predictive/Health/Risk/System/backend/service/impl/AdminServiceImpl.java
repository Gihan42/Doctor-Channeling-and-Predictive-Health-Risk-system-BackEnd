package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomAdminException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AdminRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepo adminRepo;
    private final ModelMapper modelMapper;
    private final UserRolesRepo rolesRepo;

    @Override
    public Admin saveAdmin(AdminDTO adminDTO,String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Admin adminByEmail = adminRepo.findAdminByEmail(adminDTO.getEmail());
        long roleIdByRole = rolesRepo.getRoleIdByRole(type);
        if (Objects.equals(adminByEmail,null)){
            adminDTO.setUniqId(generateAdminId());
            adminDTO.setStatus("Active");
            adminDTO.setRoleId(roleIdByRole);
            Admin map = modelMapper.map(adminDTO, Admin.class);
            map.setPassword(BCrypt.hashpw(adminDTO.getPassword(), BCrypt.gensalt(10)));
            return adminRepo.save(map);
        }
        throw new CustomAdminException("Admin already exits ");
    }

    @Override
    public String generateAdminId(){
        long activeAdminCount = adminRepo.getActiveAdminCount();
        return "AD-"+(activeAdminCount+1);
    }

    @Override
    public Admin updateAdmin(AdminDTO adminDTO, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Admin adminById = adminRepo.getAdminById(adminDTO.getId());
        long roleIdByRole = rolesRepo.getRoleIdByRole(type);
        if (!Objects.equals(adminById,null)){
            adminDTO.setUniqId(adminById.getUniqId());
            adminDTO.setStatus("Active");
            adminDTO.setRoleId(roleIdByRole);
            adminDTO.setPassword(adminById.getPassword());
            Admin map = modelMapper.map(adminDTO, Admin.class);
            return adminRepo.save(map);
        }
        throw new CustomAdminException("Admin not found ");
    }

    @Override
    public List<AdminDTO> getAllActiveAdmins(String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return modelMapper.map(adminRepo.getAllActiveAdmins(),new TypeToken<ArrayList<AdminDTO>>(){}.getType());
    }

    @Override
    public Admin deleteAdmin(long id, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Admin adminById = adminRepo.getAdminById(id);
        long roleIdByRole = rolesRepo.getRoleIdByRole(type);
        if (!Objects.equals(adminById,null)){
            adminById.setUniqId(adminById.getUniqId());
            adminById.setStatus("InActive");
            adminById.setRoleId(roleIdByRole);
            adminById.setPassword(adminById.getPassword());
            return adminRepo.save(adminById);
        }
        throw new CustomAdminException("Admin not found ");
    }

    @Override
    public Admin getAdminByEmail(String email, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Admin adminByEmail = adminRepo.getAdminByEmail(email);
        if (!Objects.equals(adminByEmail,null)){
            return adminByEmail;
        }
        throw new CustomAdminException("Admin not found ");

    }

    @Override
    public String updateUserPassword(UserPasswordDTO dto, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Admin adminById = adminRepo.getAdminById(dto.getUserId());
        boolean checkpw = BCrypt.checkpw(dto.getCurrentPassword(), adminById.getPassword());
        if(checkpw){
            adminById.setPassword(BCrypt.hashpw(dto.getNewPassword(),BCrypt.gensalt(10)));
            return "password change";
        }
        throw new CustomAdminException("user current password is wrong! ");
    }

    @Override
    public Admin getAdminById(long id, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        return adminRepo.getAdminById(id);
    }
}
