package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.UserProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AdminRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class userDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private UserRolesRepo userRoleDetailsRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserProjection userByEmail = adminRepo.findUserByEmail(email);
        String password = "";
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        String roleById = userRoleDetailsRepo.findRoleById(userByEmail.getRoleId());

        if (!Objects.equals(userByEmail,null)){
            authorities.add(new SimpleGrantedAuthority(roleById));
            password = userByEmail.getPassword();
        }

        return new org.springframework.security.core.userdetails.User(email, password, authorities);
    }
}
