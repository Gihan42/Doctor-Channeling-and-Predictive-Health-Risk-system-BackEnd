package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.ChannelingRoomDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.MedicalCenterDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterWithTypeProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCentersAndIds;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomMedicalCenterException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.MedicalCenterTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalCenterServiceImpl implements MedicalCenterService {


    private final MedicalCenterRepo medicalCenterRepo;
    private  final ModelMapper modelMapper;
    private final MedicalCenterTypeService medicalCenterTypeService;


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
            MedicalCenter map = modelMapper.map(medicalCenterDTO, MedicalCenter.class);
            long centerTypeId = medicalCenterTypeService.addMedicalCenterType(medicalCenterDTO.getMedicalCenterType());
            map.setCenterTypeId(centerTypeId);
            map.setStatus("Active");
            map.setChannelingRooms(medicalCenterDTO.getChannelingRooms());
            return medicalCenterRepo.save(map);
        }

        throw new CustomMedicalCenterException("Medical Center already exists");
    }

    @Override
    public MedicalCenter updateMedicalCenter(MedicalCenterDTO medicalCenterDTO, String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        MedicalCenter existingCenter = medicalCenterRepo.findMedicalCenterById(medicalCenterDTO.getId());
        if (Objects.equals(existingCenter,null)) {
            throw new CustomMedicalCenterException("Medical Center already exists");
        }
        existingCenter.setCenterName(medicalCenterDTO.getCenterName());
        existingCenter.setRegistrationNumber(existingCenter.getRegistrationNumber());
        existingCenter.setContact1(medicalCenterDTO.getContact1());
        existingCenter.setContact2(medicalCenterDTO.getContact2());
        existingCenter.setEmail(medicalCenterDTO.getEmail());
        existingCenter.setAddress(medicalCenterDTO.getAddress());
        existingCenter.setDistric(medicalCenterDTO.getDistric());
        existingCenter.setOpenTime(medicalCenterDTO.getOpenTime());
        existingCenter.setCloseTime(medicalCenterDTO.getCloseTime());
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

        return medicalCenterRepo.save(existingCenter);
    }

    @Override
    public List<MedicalCentersAndIds> getMedicalCenterByCity(String districtName,String type) {
        if (!type.equals("Admin")&& !type.equals("Patient")) {
            throw new CustomBadCredentialsException("Don't have permission");
        }
        return medicalCenterRepo.getMedicalCenterByDistrict(districtName);
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

}
