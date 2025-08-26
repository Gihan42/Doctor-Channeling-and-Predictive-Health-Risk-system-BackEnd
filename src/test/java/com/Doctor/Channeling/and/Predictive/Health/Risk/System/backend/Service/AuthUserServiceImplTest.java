package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.Service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AuthenticationRequestDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.UserProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AdminRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.AuthUserServiceImpl;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.JWTUtil;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserServiceImplTest {

    @Mock
    private AdminRepo userRepo;

    @Mock
    private UserRolesRepo userRolesRepo;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthUserServiceImpl authUserService;

    private AuthenticationRequestDTO authRequestDTO;
    private UserProjection userProjection;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Arrange common objects, but NOT specific mock behaviors
        authRequestDTO = new AuthenticationRequestDTO();
        authRequestDTO.setEmail("testuser@example.com");
        authRequestDTO.setPassword("password123");

        // Create the mock object here, but don't define its behavior yet
        userProjection = mock(UserProjection.class);

        userDetails = new User("testuser@example.com", "password123", Collections.emptyList());
    }

    @Test
    @DisplayName("Should return LoginResponse on successful authentication")
    void logUser_Success() {
        // Arrange
        String fakeJwt = "fake.jwt.token";
        String userRole = "Patient";
        long numericUserId = 101L;

        // --- ADD STUBS HERE for the success path ---
        when(userProjection.getEmail()).thenReturn("testuser@example.com");
        when(userProjection.getFullName()).thenReturn("Test User");
        when(userProjection.getUserId()).thenReturn("USR-001");
        when(userProjection.getRoleId()).thenReturn(1L);
        // ------------------------------------------

        when(userRepo.findUserByEmail(authRequestDTO.getEmail())).thenReturn(userProjection);
        when(userRolesRepo.findRoleById(userProjection.getRoleId())).thenReturn(userRole);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(authRequestDTO.getEmail())).thenReturn(userDetails);
        when(userRepo.findUserIdByUniqId(userProjection.getUserId())).thenReturn(numericUserId);
        when(jwtUtil.generateToken(userDetails, userRole)).thenReturn(fakeJwt);

        // Act
        LoginResponse loginResponse = authUserService.logUser(authRequestDTO);

        // Assert
        assertNotNull(loginResponse);
        assertEquals(200, loginResponse.getCode());
        assertEquals("Login Success", loginResponse.getMessage());
        assertEquals(fakeJwt, loginResponse.getJwt());
        assertEquals("Test User", loginResponse.getUserName());
        assertEquals(userRole, loginResponse.getRole());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("Should throw CustomBadCredentialsException if user is not found")
    void logUser_FailsWhenUserNotFound() {
        // Arrange
        when(userRepo.findUserByEmail(authRequestDTO.getEmail())).thenReturn(null);

        // Act & Assert
        CustomBadCredentialsException exception = assertThrows(CustomBadCredentialsException.class, () -> {
            authUserService.logUser(authRequestDTO);
        });

        assertEquals("invalid details", exception.getMessage());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Should throw CustomBadCredentialsException for incorrect password")
    void logUser_FailsForIncorrectPassword() {
        // Arrange
        // --- ADD THE REQUIRED STUB HERE ---
        // This test only needs getRoleId() to be stubbed before the exception is thrown
        when(userProjection.getRoleId()).thenReturn(1L);
        // ----------------------------------

        when(userRepo.findUserByEmail(authRequestDTO.getEmail())).thenReturn(userProjection);
        when(userRolesRepo.findRoleById(userProjection.getRoleId())).thenReturn("Patient");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        CustomBadCredentialsException exception = assertThrows(CustomBadCredentialsException.class, () -> {
            authUserService.logUser(authRequestDTO);
        });

        assertEquals("invalid details", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}