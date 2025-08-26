package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomAdminException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AdminRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminRepo adminRepo;

    @Mock
    private UserRolesRepo rolesRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private AdminDTO adminDTO;
    private Admin admin;
    private static final String ADMIN_ROLE_TYPE = "Admin";
    private static final String NON_ADMIN_ROLE_TYPE = "User";

    @BeforeEach
    void setUp() {
        // Setup common test data
        adminDTO = new AdminDTO();
        adminDTO.setId(1L);
        adminDTO.setEmail("test@admin.com");
        adminDTO.setPassword("password123");
        adminDTO.setFullName("Test");

        admin = new Admin();
        admin.setId(1L);
        admin.setEmail("test@admin.com");
        admin.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt(10)));
        adminDTO.setFullName("Test");
        admin.setStatus("Active");
        admin.setUniqId("AD-1");
    }

    // ================== saveAdmin Tests ==================

    @Test
    @DisplayName("Should save a new admin successfully")
    void saveAdmin_Success() {
        // Arrange
        when(rolesRepo.getRoleIdByRole(ADMIN_ROLE_TYPE)).thenReturn(1L);
        when(adminRepo.findAdminByEmail(adminDTO.getEmail())).thenReturn(null);
        when(adminRepo.getActiveAdminCount()).thenReturn(0L);
        when(modelMapper.map(any(AdminDTO.class), eq(Admin.class))).thenReturn(admin);
        when(adminRepo.save(any(Admin.class))).thenReturn(admin);

        // Act
        Admin savedAdmin = adminService.saveAdmin(adminDTO, ADMIN_ROLE_TYPE);

        // Assert
        assertNotNull(savedAdmin);
        assertEquals("AD-1", savedAdmin.getUniqId());
        assertEquals("Active", savedAdmin.getStatus());
        assertTrue(BCrypt.checkpw("password123", savedAdmin.getPassword()));
        verify(adminRepo, times(1)).findAdminByEmail(adminDTO.getEmail());
        verify(adminRepo, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw CustomAdminException when saving an existing admin")
    void saveAdmin_AdminAlreadyExists_ThrowsException() {
        // Arrange
        when(rolesRepo.getRoleIdByRole(ADMIN_ROLE_TYPE)).thenReturn(1L);
        when(adminRepo.findAdminByEmail(adminDTO.getEmail())).thenReturn(admin);

        // Act & Assert
        CustomAdminException exception = assertThrows(CustomAdminException.class, () -> {
            adminService.saveAdmin(adminDTO, ADMIN_ROLE_TYPE);
        });
        assertEquals("Admin already exits ", exception.getMessage());
        verify(adminRepo, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw CustomBadCredentialsException for non-admin type on save")
    void saveAdmin_NoPermission_ThrowsException() {
        // Act & Assert
        CustomBadCredentialsException exception = assertThrows(CustomBadCredentialsException.class, () -> {
            adminService.saveAdmin(adminDTO, NON_ADMIN_ROLE_TYPE);
        });
        assertEquals("dont have permission", exception.getMessage());
    }

    // ================== generateAdminId Test ==================

    @Test
    @DisplayName("Should generate admin ID correctly")
    void generateAdminId_Success() {
        // Arrange
        when(adminRepo.getActiveAdminCount()).thenReturn(5L);

        // Act
        String generatedId = adminService.generateAdminId();

        // Assert
        assertEquals("AD-6", generatedId);
    }

    // ================== updateAdmin Tests ==================

    @Test
    @DisplayName("Should update an admin successfully")
    void updateAdmin_Success() {
        // Arrange
        when(adminRepo.getAdminById(adminDTO.getId())).thenReturn(admin);
        when(rolesRepo.getRoleIdByRole(ADMIN_ROLE_TYPE)).thenReturn(1L);
        when(modelMapper.map(any(AdminDTO.class), eq(Admin.class))).thenReturn(admin);
        when(adminRepo.save(any(Admin.class))).thenReturn(admin);

        // Act
        Admin updatedAdmin = adminService.updateAdmin(adminDTO, ADMIN_ROLE_TYPE);

        // Assert
        assertNotNull(updatedAdmin);
        assertEquals(admin.getUniqId(), updatedAdmin.getUniqId());
        verify(adminRepo, times(1)).getAdminById(adminDTO.getId());
        verify(adminRepo, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw CustomAdminException when updating a non-existent admin")
    void updateAdmin_NotFound_ThrowsException() {
        // Arrange
        when(adminRepo.getAdminById(adminDTO.getId())).thenReturn(null);
        when(rolesRepo.getRoleIdByRole(ADMIN_ROLE_TYPE)).thenReturn(1L);

        // Act & Assert
        CustomAdminException exception = assertThrows(CustomAdminException.class, () -> {
            adminService.updateAdmin(adminDTO, ADMIN_ROLE_TYPE);
        });
        assertEquals("Admin not found ", exception.getMessage());
    }

    // ================== getAllActiveAdmins Tests ==================

    @Test
    @DisplayName("Should get all active admins")
    void getAllActiveAdmins_Success() {
        // Arrange
        List<Admin> adminList = Collections.singletonList(admin);
        List<AdminDTO> adminDTOList = Collections.singletonList(adminDTO);

        when(adminRepo.getAllActiveAdmins()).thenReturn(adminList);
        // FIX: Changed List<AdminDTO> to ArrayList<AdminDTO> to match the service implementation
        when(modelMapper.map(adminList, new TypeToken<ArrayList<AdminDTO>>(){}.getType())).thenReturn(adminDTOList);

        // Act
        List<AdminDTO> result = adminService.getAllActiveAdmins(ADMIN_ROLE_TYPE);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(adminDTO.getEmail(), result.get(0).getEmail());
    }

    // ================== deleteAdmin Tests ==================

    @Test
    @DisplayName("Should delete (deactivate) an admin successfully")
    void deleteAdmin_Success() {
        // Arrange
        when(adminRepo.getAdminById(admin.getId())).thenReturn(admin);
        when(rolesRepo.getRoleIdByRole(ADMIN_ROLE_TYPE)).thenReturn(1L);
        when(adminRepo.save(any(Admin.class))).thenReturn(admin);

        // Act
        Admin deletedAdmin = adminService.deleteAdmin(admin.getId(), ADMIN_ROLE_TYPE);

        // Assert
        assertNotNull(deletedAdmin);
        assertEquals("InActive", deletedAdmin.getStatus());
        verify(adminRepo, times(1)).save(admin);
    }

    // ================== updateUserPassword Tests ==================

    @Test
    @DisplayName("Should update user password successfully")
    void updateUserPassword_Success() {
        // Arrange
        UserPasswordDTO passwordDTO = new UserPasswordDTO();
        passwordDTO.setUserId(admin.getId());
        passwordDTO.setCurrentPassword("password123"); // The correct current password
        passwordDTO.setNewPassword("newPassword456");

        when(adminRepo.getAdminById(admin.getId())).thenReturn(admin);

        // Act
        String result = adminService.updateUserPassword(passwordDTO, ADMIN_ROLE_TYPE);

        // Assert
        assertEquals("password change", result);
        assertTrue(BCrypt.checkpw("newPassword456", admin.getPassword()));
    }

    @Test
    @DisplayName("Should throw CustomAdminException for wrong current password")
    void updateUserPassword_WrongCurrentPassword_ThrowsException() {
        // Arrange
        UserPasswordDTO passwordDTO = new UserPasswordDTO();
        passwordDTO.setUserId(admin.getId());
        passwordDTO.setCurrentPassword("wrongPassword"); // The incorrect current password
        passwordDTO.setNewPassword("newPassword456");

        when(adminRepo.getAdminById(admin.getId())).thenReturn(admin);

        // Act & Assert
        CustomAdminException exception = assertThrows(CustomAdminException.class, () -> {
            adminService.updateUserPassword(passwordDTO, ADMIN_ROLE_TYPE);
        });
        assertEquals("user current password is wrong! ", exception.getMessage());
    }
}