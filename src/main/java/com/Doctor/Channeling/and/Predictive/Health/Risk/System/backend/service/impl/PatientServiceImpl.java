package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AdminDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PatientDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SignUpDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.UserPasswordDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomAdminException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomPatientException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AdminRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PatientRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AdminService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.PatientService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.JWTUtil;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepo patientRepo;
    private final ModelMapper modelMapper;
    private final UserRolesRepo rolesRepo;
    private final UserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AdminRepo adminRepo;
    private final RestTemplate restTemplate;



    @Override
    public LoginResponse singUp(SignUpDTO signUpDTO) {
        Patient byPatientEmail = patientRepo.findByPatientEmail(signUpDTO.getEmail());
        long roleIdByRole = rolesRepo.getRoleIdByRole("Patient");
        if(Objects.equals(byPatientEmail,null)){
            Patient patient = new Patient(
                    (long)0,
                    generatePatientId(),
                    signUpDTO.getUserName(),
                    signUpDTO.getEmail(),
                    BCrypt.hashpw(signUpDTO.getPassword(), BCrypt.gensalt(10)),
                    "Empty",
                    null,
                    0,
                    signUpDTO.getPhoneNumber(),
                    "Empty",
                    "Empty",
                    "Active",
                    roleIdByRole
            );
            Patient savedPatient = patientRepo.save(patient);
            LoginResponse loginResponse = new LoginResponse();
            Authentication authenticate =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    savedPatient.getEmail(), signUpDTO.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedPatient.getEmail());
            String generateToken = jwtUtil.generateToken(userDetails, "Patient");
            long userIdByUniqId = adminRepo.findUserIdByUniqId(savedPatient.getUniqId());

            loginResponse.setMessage("User Created Successfully");
            loginResponse.setJwt(generateToken);
            loginResponse.setUserName(savedPatient.getFullName());
            loginResponse.setUserId(savedPatient.getUniqId());
            loginResponse.setEmail(savedPatient.getEmail());
            loginResponse.setRole("Patient");
            loginResponse.setId(userIdByUniqId);
            loginResponse.setCode(200);
            return loginResponse;

        }
        throw new CustomBadCredentialsException("User Already Exist");
    }

    @Override
    public String generatePatientId() {
        long countActivePatientCount = patientRepo.countPatientStatus();
        return "PT-"+(countActivePatientCount+1);
    }

    @Override
    public Patient updatePatient(PatientDTO patientDTO, String type) {
        if (!type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }

        Patient byPatientId = patientRepo.findByPatientId(patientDTO.getId());
        long patient = rolesRepo.getRoleIdByRole("Patient");
        if (!Objects.equals(byPatientId,null)){
            patientDTO.setUniqId(byPatientId.getUniqId());
            patientDTO.setStatus("Active");
            patientDTO.setRoleId(patient);
            patientDTO.setPassword(byPatientId.getPassword());
            Patient map = modelMapper.map(patientDTO, Patient.class);
            return patientRepo.save(map);
        }
        throw new CustomPatientException("Patient not found ");
    }

    @Override
    public List<PatientDTO> getAllActivePatients(String type) {
        if (!type.equals("Admin")){
                throw new CustomBadCredentialsException("dont have permission");
            }
        return modelMapper.map(patientRepo.getAllActivePatients(),new TypeToken<ArrayList<PatientDTO>>(){}.getType());

    }

    @Override
    public Patient deletePatient(long id, String type) {
        if (!type.equals("Patient") && !type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }

        Patient byPatientId = patientRepo.findByPatientId(id);
        if (!Objects.equals(byPatientId, null)) {
            byPatientId.setStatus("InActive");
            return patientRepo.save(byPatientId);
        }
        throw new CustomPatientException("Patient not found ");

    }

    @Override
    public Patient getPatientByEmail(String email, String type) {
        if (!type.equals("Patient") && !type.equals("Admin") && !type.equals("Doctor")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Patient patientByEmail = patientRepo.findByPatientEmail(email);
        if (!Objects.equals(patientByEmail, null)) {
            return patientByEmail;
        }
        throw new CustomPatientException("Patient not found ");
    }

    @Override
    public String updateUserPassword(UserPasswordDTO dto, String type) {
        if (!type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Patient byPatientId = patientRepo.findByPatientId(dto.getUserId());
        boolean checkpw = BCrypt.checkpw(dto.getCurrentPassword(), byPatientId.getPassword());
        if(checkpw){
            byPatientId.setPassword(BCrypt.hashpw(dto.getNewPassword(),BCrypt.gensalt(10)));
            return "password change";
        }
        throw new CustomAdminException("user current password is wrong! ");
    }

    @Override
    public List<String>  getPatientNearByCity(long id, String type) {
        if (!type.equals("Patient")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        Patient byPatientId = patientRepo.findByPatientId(id);
        if (!Objects.equals(byPatientId, null)) {
            try{
                String encodedLocation = URLEncoder.encode(byPatientId.getCity(), StandardCharsets.UTF_8);
                String url = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + ",Sri+Lanka&format=json&addressdetails=1";
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "YourApp/1.0 (contact@example.com)");
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class);

                System.out.println("Response: " + response.getBody());

                return parseNominatimResponse(response.getBody());

            }catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
       throw new CustomPatientException("Patient not found ");
    }
    private List<String> parseNominatimResponse(String json) {
        List<String> cities = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    JsonNode address = node.path("address");
                    String city = address.path("city").asText();

                    if (city != null && !city.isEmpty()) {
                        System.out.println("City: " + city);  // This will print "Colombo"
                        cities.add(city);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return only distinct cities, top 3 max
        return cities.stream().distinct().limit(3).toList();
    }


}
