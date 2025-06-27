package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ChannelingRoomProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterWithTypeProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCentersAndIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalCenterRepo extends JpaRepository<MedicalCenter, Long> {

    @Query(value = "    select * from medicle_center where medicle_center_id = :id and status ='Active'",nativeQuery = true)
    MedicalCenter findMedicalCenterById(@Param("id") long id );

    @Query(value = "    select * from medicle_center where registration_number = :registrationNo and status = 'Active'",nativeQuery = true)
    MedicalCenter findMedicalCenterByRegistrationNumber(@Param("registrationNo") String registrationNo);

    @Query(value = "SELECT\n" +
            "    room.*\n" +
            "FROM\n" +
            "    medicle_center mc,\n" +
            "    JSON_TABLE(\n" +
            "            mc.channeling_rooms,\n" +
            "            '$[*]' COLUMNS (\n" +
            "                room_id BIGINT PATH '$.id',\n" +
            "                room_name VARCHAR(255) PATH '$.roomName',\n" +
            "                description VARCHAR(255) PATH '$.description',\n" +
            "                status VARCHAR(255) PATH '$.status'\n" +
            "                )\n" +
            "    ) AS room\n" +
            "WHERE\n" +
            "    mc.status ='Active'\n" +
            "  AND room.room_id = :roomId",nativeQuery = true)
    ChannelingRoomProjection getMedicalCenterByChannelingRoom(@Param("roomId") long roomId);

    @Query(value = """
    SELECT
        room.room_id AS roomId,
        room.room_name AS roomName,
        room.description AS description,
        room.status AS status
    FROM
        medicle_center mc,
        JSON_TABLE(
            mc.channeling_rooms,
            '$[*]' COLUMNS (
                room_id BIGINT PATH '$.id',
                room_name VARCHAR(255) PATH '$.roomName',
                description VARCHAR(255) PATH '$.description',
                status VARCHAR(255) PATH '$.status'
            )
        ) AS room
    WHERE
        mc.medicle_center_id = :id AND mc.status = 'Active'
""", nativeQuery = true)
    List<ChannelingRoomProjection> getAllChannelingRoomsByMedicalCenterId(@Param("id") long id);

/*    @Query(value = "select medicle_center_id as Id , center_name as Name from medicle_center where distric= :district and status = 'Active'",nativeQuery = true)
    List<MedicalCentersAndIds> getMedicalCenterByDistrict(@Param("district") String district);*/

    @Query(value = """
        SELECT
            medicle_center_id AS Id,
            center_name AS centerName,
            contact1 AS contact1,
            contact2 AS contact2,
            email AS email,
            address AS address,
            distric AS distric,
            open_time AS openTime,
            close_time AS closeTime,
            channeling_fee AS channelingFee,
            center_type_id AS centerType
        FROM medicle_center
        WHERE distric = :district
          AND status = 'Active'
""", nativeQuery = true)
    List<MedicalCentersAndIds> getMedicalCenterByDistrict(@Param("district") String district);


    @Query(value = "SELECT " +
            "mc.medicle_center_id AS id, " +
            "mc.center_name AS centerName, " +
            "mc.registration_number AS registrationNumber, " +
            "mc.contact1 AS contact1, " +
            "mc.contact2 AS contact2, " +
            "mc.email AS email, " +
            "mc.address AS address, " +
            "mc.distric AS distric, " +
            "mc.status AS status, " +
            "mc.channeling_fee AS channelingFee, " +
            "mc.center_type_id AS centerTypeId, " +
            "mct.center_type AS centerType " +
            "FROM medicle_center mc " +
            "JOIN medicle_center_type mct ON mc.center_type_id = mct.medicle_center_type_id " +
            "WHERE mc.status = 'Active' AND mct.status = 'Active'", nativeQuery = true)
    List<MedicalCenterWithTypeProjection> getAllActiveMedicalCentersWithType();

    @Query(value = "SELECT " +
            "mc.medicle_center_id AS id, " +
            "mc.center_name AS centerName, " +
            "mc.registration_number AS registrationNumber, " +
            "mc.contact1 AS contact1, " +
            "mc.contact2 AS contact2, " +
            "mc.email AS email, " +
            "mc.address AS address, " +
            "mc.distric AS distric, " +
            "mc.status AS status, " +
            "mc.channeling_fee AS channelingFee, " +
            "mc.center_type_id AS centerTypeId, " +
            "mct.center_type AS centerType " +
            "FROM medicle_center mc " +
            "JOIN medicle_center_type mct ON mc.center_type_id = mct.medicle_center_type_id " +
            "WHERE mc.status = 'Active'AND mc.registration_number = :registrationNumber AND mct.status = 'Active'", nativeQuery = true)
    MedicalCenterWithTypeProjection getActiveMedicalCentersWithTypeByRegistrationNumber(@Param("registrationNumber") String registrationNumber);

    @Query(value = "select medicle_center_id as Id , center_name as Name from medicle_center where  status = 'Active'",nativeQuery = true)
    List<MedicalCentersAndIds> getAllActiveMedicalCentersAndIds();
}
