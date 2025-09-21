package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.ChannelingRoomDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.MedicalCenterDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterWithTypeProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCentersAndIds;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomMedicalCenterException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PatientRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterTypeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalCenterServiceImpl implements MedicalCenterService {


    private final MedicalCenterRepo medicalCenterRepo;
    private  final ModelMapper modelMapper;
    private final MedicalCenterTypeService medicalCenterTypeService;
    private final PatientRepo patientRepo;
    private final RestTemplate restTemplate;

    @Override
    public MedicalCenter createMedicalCenter(MedicalCenterDTO medicalCenterDTO, String type) {
        if (!type.equals("Admin")){
            throw new CustomBadCredentialsException("dont have permission");
        }
        MedicalCenter medicalCenterById =
                medicalCenterRepo.findMedicalCenterById(medicalCenterDTO.getId());

        MedicalCenter medicalCenterByRegistrationNumber =
                medicalCenterRepo.findMedicalCenterByRegistrationNumber(medicalCenterDTO.getRegistrationNumber());

        if (Objects.equals(medicalCenterById,null) &&
                                Objects.equals(medicalCenterByRegistrationNumber,null)) {

            List<ChannelingRoomDTO> roomList = medicalCenterDTO.getChannelingRooms();
            if (roomList != null) {
                Random random = new Random();
                for (ChannelingRoomDTO room : roomList) {
                    long newId;
                    boolean exists;
                    do {
                        newId = 10000 + random.nextInt(90000);
                        ChannelingRoomProjection existingRoom =
                                medicalCenterRepo.getMedicalCenterByChannelingRoom(newId);
                        exists = existingRoom != null;
                    } while (exists);

                    room.setId(newId);
                }
            }
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date fullDate = timeFormat.parse(medicalCenterDTO.getOpenTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fullDate);
                calendar.set(1970, Calendar.JANUARY, 1);
                Date openTime = calendar.getTime();

                Date fullDateTwo = timeFormat.parse(medicalCenterDTO.getCloseTime());
                Calendar calendarTwo = Calendar.getInstance();
                calendarTwo.setTime(fullDateTwo);
                calendarTwo.set(1970, Calendar.JANUARY, 1);
                Date closeTime = calendarTwo.getTime();

                MedicalCenter map = modelMapper.map(medicalCenterDTO, MedicalCenter.class);
                long centerTypeId = medicalCenterTypeService.addMedicalCenterType(medicalCenterDTO.getMedicalCenterType());
                map.setCenterTypeId(centerTypeId);
                map.setStatus("Active");
                map.setOpenTime(openTime);
                map.setCloseTime(closeTime);
                map.setChannelingRooms(medicalCenterDTO.getChannelingRooms());
                MedicalCenter save = medicalCenterRepo.save(map);
                if (roomList != null) {
                    for (ChannelingRoomDTO room : roomList) {
                        room.setMedicalCenterId(save.getId());
                    }
                    save.setChannelingRooms(roomList);
                    save = medicalCenterRepo.save(save);
                }
                return save;

            } catch (ParseException e) {
                throw new RuntimeException("Time format is invalid. Expected format: HH:mm:ss", e);
            }
        }

        throw new CustomMedicalCenterException("Medical Center already exists");
    }

    @Override
    public MedicalCenter updateMedicalCenter(MedicalCenterDTO medicalCenterDTO, String type)  {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        MedicalCenter existingCenter = medicalCenterRepo.findMedicalCenterById(medicalCenterDTO.getId());
        if (Objects.equals(existingCenter,null)) {
            throw new CustomMedicalCenterException("Medical Center already exists");
        }
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date fullDate = timeFormat.parse(medicalCenterDTO.getOpenTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fullDate);
            calendar.set(1970, Calendar.JANUARY, 1);
            Date openTime = calendar.getTime();

            Date fullDateTwo = timeFormat.parse(medicalCenterDTO.getCloseTime());
            Calendar calendarTwo = Calendar.getInstance();
            calendarTwo.setTime(fullDateTwo);
            calendarTwo.set(1970, Calendar.JANUARY, 1);
            Date closeTime = calendarTwo.getTime();

            existingCenter.setCenterName(medicalCenterDTO.getCenterName());
            existingCenter.setRegistrationNumber(existingCenter.getRegistrationNumber());
            existingCenter.setContact1(medicalCenterDTO.getContact1());
            existingCenter.setContact2(medicalCenterDTO.getContact2());
            existingCenter.setEmail(medicalCenterDTO.getEmail());
            existingCenter.setAddress(medicalCenterDTO.getAddress());
            existingCenter.setDistric(medicalCenterDTO.getDistric());
            existingCenter.setOpenTime(openTime);
            existingCenter.setCloseTime(closeTime);
            existingCenter.setChannelingFee(medicalCenterDTO.getChannelingFee());

            long centerTypeId = medicalCenterTypeService.addMedicalCenterType(medicalCenterDTO.getMedicalCenterType());
            existingCenter.setCenterTypeId(centerTypeId);

            existingCenter.setStatus(medicalCenterDTO.getStatus());

            List<ChannelingRoomDTO> incomingRooms = medicalCenterDTO.getChannelingRooms();
            if (incomingRooms != null) {
                for (ChannelingRoomDTO room : incomingRooms) {
                    if (room.getId() == null || room.getId() == 0) {
                        long newId;
                        boolean exists;
                        Random random = new Random();
                        do {
                            newId = 10000 + random.nextInt(90000);
                            ChannelingRoomProjection existingRoom = medicalCenterRepo.getMedicalCenterByChannelingRoom(newId);
                            exists = existingRoom != null;
                        } while (exists);
                        room.setId(newId);
                    }
                }
                existingCenter.setChannelingRooms(incomingRooms);
            }
            MedicalCenter save = medicalCenterRepo.save(existingCenter);
            if (incomingRooms != null) {
                for (ChannelingRoomDTO room : incomingRooms) {
                    room.setMedicalCenterId(save.getId());
                }
                save.setChannelingRooms(incomingRooms);
                save = medicalCenterRepo.save(save);
            }
            return save;
        }catch (ParseException e) {
                throw new RuntimeException("Time format is invalid. Expected format: HH:mm:ss", e);
            }
    }

    @Override
    public List<MedicalCentersAndIds> getMedicalCenterByCity(long patientId, String type) {
        if (!type.equals("Admin") && !type.equals("Patient")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }

        Patient byPatientId = patientRepo.findByPatientId(patientId);

        if (byPatientId != null) {
            try {
                String encodedLocation = URLEncoder.encode(byPatientId.getCity(), StandardCharsets.UTF_8);
                String url = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + ",Sri+Lanka&format=json&addressdetails=1";

                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "YourApp/1.0 (contact@example.com)");
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                String city = parseNominatimResponse(response.getBody());


                    String districtName = city;
                    return medicalCenterRepo.getMedicalCenterByDistrict(districtName);


            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }

        throw new CustomMedicalCenterException("No medical center found");
    }

    private String parseNominatimResponse(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);
            if (root.isArray() && root.size() > 0) {
                JsonNode address = root.get(0).path("address");

                String district = address.path("state_district").asText();

                if (district != null && !district.isEmpty()) {
                    String cleanedDistrict = district.replace(" District", "").trim();
                    return cleanedDistrict;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new CustomMedicalCenterException("No valid district found in nominatim response.");
    }


    @Override
    public List<MedicalCenterWithTypeProjection> getAllActiveMedicalCenters(String type) {
        if (!type.equals("Admin")&& !type.equals("Patient")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return medicalCenterRepo.getAllActiveMedicalCentersWithType();
    }

    @Override
    public MedicalCenterWithTypeProjection getMedicalCenterByRegistrationId(String registrationNo, String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return medicalCenterRepo.getActiveMedicalCentersWithTypeByRegistrationNumber(registrationNo);
    }

    @Override
    public MedicalCenter deleteMedicalCenter(long id, String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }

        MedicalCenter medicalCenterById = medicalCenterRepo.findMedicalCenterById(id);

        if (medicalCenterById != null && !"Inactive".equalsIgnoreCase(medicalCenterById.getStatus())) {
            medicalCenterById.setStatus("Inactive");
            return medicalCenterRepo.save(medicalCenterById);
        }

        throw new CustomMedicalCenterException("Medical Center not found or already inactive");
    }

    @Override
    public List<ChannelingRoomProjection> getAllChannelingRoomsByMedicalCenterId(long id, String type) {
        if (!type.equals("Admin")&& !type.equals("Patient")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return  medicalCenterRepo.getAllChannelingRoomsByMedicalCenterId(id);

    }

    @Override
    public List<MedicalCentersAndIds> getAllMedicalCentersAndIds(String type) {
        if (!type.equals("Admin")&& !type.equals("Patient")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return medicalCenterRepo.getAllActiveMedicalCentersAndIds();
    }

    @Override
    public MedicalCenter findMedicalCenterById(long id, String type) {
        if (!type.equals("Admin")&& !type.equals("Patient")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return medicalCenterRepo.findMedicalCenterById(id);
    }

    @Override
    public int getCountOfMedicalCenters(String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return medicalCenterRepo.getActiveMedicalCenterCount();
    }

}
