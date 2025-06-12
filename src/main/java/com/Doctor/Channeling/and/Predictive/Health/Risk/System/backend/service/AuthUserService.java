package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AuthenticationRequestDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl.AuthUserServiceImpl;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


public interface AuthUserService {

    LoginResponse logUser(AuthenticationRequestDTO dto);
}
