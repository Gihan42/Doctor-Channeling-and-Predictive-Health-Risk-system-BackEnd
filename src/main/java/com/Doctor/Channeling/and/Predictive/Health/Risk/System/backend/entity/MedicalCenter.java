package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.ChannelingRoomDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "medicle_center")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicle_center_id")
    private Long id;

    @Column(name = "center_name", length = 255)
    @NonNull
    private String centerName;

    @Column(name = "registration_number", length = 255)
    @NonNull
    private String registrationNumber;

    @Column(name = "contact1", length = 255)
    @NonNull
    private String contact1;

    @Column(name = "contact2", length = 255)
    @NonNull
    private String contact2;

    @Column(name = "email", length = 255)
    @NonNull
    private String email;

    @Column(name = "address", length = 255)
    @NonNull
    private String address;

    @Column(name = "distric", length = 255)
    @NonNull
    private String distric;

    @Column(name = "openTime", length = 255)
    @Temporal(TemporalType.TIME) //
    @NonNull
    private Date openTime;

    @Column(name = "closeTime", length = 255)
    @Temporal(TemporalType.TIME) //
    @NonNull
    private Date closeTime;

    @Column(name = "channeling_fee")
    @NonNull
    private double channelingFee;

    @Column(name = "center_type_id")
    @NonNull
    private long centerTypeId;

    @Column(name = "status", length = 255)
    private String status;

    @Lob
    @Column(name = "channeling_rooms", columnDefinition = "TEXT")
    private String channelingRoomsJson;

    @Transient
    private List<ChannelingRoomDTO> channelingRooms;

    public void setChannelingRooms(List<ChannelingRoomDTO> rooms) {
        this.channelingRooms = rooms;
        try {
            this.channelingRoomsJson = new ObjectMapper().writeValueAsString(rooms);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert rooms to JSON", e);
        }
    }

    public void parseChannelingRoomsFromJson() {
        try {
            this.channelingRooms = new ObjectMapper().readValue(this.channelingRoomsJson,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ChannelingRoomDTO>>() {});
        } catch (Exception e) {
            this.channelingRooms = new ArrayList<>();
        }
    }
}
