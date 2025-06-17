package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AuthenticationRequestDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.UserProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AdminRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AuthUserService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.JWTUtil;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {
    private final AdminRepo userRepo;
    private final UserRolesRepo userRolesRepo;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;


    @Override
    public LoginResponse logUser(AuthenticationRequestDTO dto) {
        try{
            UserProjection userByEmail = userRepo.findUserByEmail(dto.getEmail());
            String roleById = userRolesRepo.findRoleById(userByEmail.getRoleId());
            if(Objects.equals(userByEmail,null)){
                throw new BadCredentialsException("invalid details");
            }

            LoginResponse loginResponse = new LoginResponse();
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userByEmail.getEmail(), dto.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(userByEmail.getEmail());
            long userIdByUniqId = userRepo.findUserIdByUniqId(userByEmail.getUserId());


            String generateToken = jwtUtil.generateToken(userDetails, roleById);
                loginResponse.setJwt(generateToken);
                loginResponse.setUserName(userByEmail.getFullName());
                loginResponse.setUserId(userByEmail.getUserId());
                loginResponse.setRole(String.valueOf(roleById));
                loginResponse.setEmail(userByEmail.getEmail());
                loginResponse.setId(userIdByUniqId);
                loginResponse.setMessage("Login Success");
                loginResponse.setCode(200);
                return loginResponse;



        }catch (Exception e){
            throw new CustomBadCredentialsException("invalid details");
        }
    }
}
