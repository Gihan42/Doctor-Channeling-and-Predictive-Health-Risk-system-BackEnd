package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.MedicalCenterDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterWithTypeProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCentersAndIds;

import java.text.ParseException;
import java.util.List;

public interface MedicalCenterService {

    MedicalCenter createMedicalCenter(MedicalCenterDTO medicalCenterDTO,String type);
    MedicalCenter updateMedicalCenter(MedicalCenterDTO medicalCenterDTO, String type) ;
    List<MedicalCentersAndIds> getMedicalCenterByCity(long patientId,String type);
    List<MedicalCenterWithTypeProjection> getAllActiveMedicalCenters(String type);
    MedicalCenterWithTypeProjection getMedicalCenterByRegistrationId(String registrationNo, String type);
    MedicalCenter deleteMedicalCenter(long id, String type);
    List<ChannelingRoomProjection> getAllChannelingRoomsByMedicalCenterId(long id, String type);
    List<MedicalCentersAndIds> getAllMedicalCentersAndIds(String type);
    MedicalCenter findMedicalCenterById(long id ,String type);

}
