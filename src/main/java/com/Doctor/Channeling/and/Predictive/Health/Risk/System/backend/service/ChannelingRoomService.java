package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;

import java.util.List;

public interface ChannelingRoomService {

    List<ChannelingRoomProjection> findAllChannelingRoomsByMedicalCenter(long id , String type);
}
