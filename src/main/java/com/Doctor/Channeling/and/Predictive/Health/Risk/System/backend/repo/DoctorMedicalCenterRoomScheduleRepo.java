package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorScheduleProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.ScheduleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorMedicalCenterRoomScheduleRepo extends JpaRepository<DoctorMedicalCenterRoomSchedule,Long> {

    @Query(value = "select * from doctor_medical_center_room_schedule where doctor_medical_center_room_schedule_id = :id and status = 'Active'",nativeQuery = true)
    DoctorMedicalCenterRoomSchedule findByIdAndStatusActive(@Param("id") long id);

    @Query(value = """
    SELECT
        dmr.doctor_medical_center_room_schedule_id AS scheduleId,
        dmr.day_of_week AS dayOfWeek,
        dmr.start_time AS startTime,
        dmr.end_time AS endTime,
        dmr.status AS status,

        mc.medicle_center_id AS medicalCenterId,
        mc.center_name AS centerName,
        mc.address AS address,

        room_obj.id AS id,
        room_obj.medicalCenterId AS medicalCenterIdRoom,
        room_obj.roomName AS roomName,
        room_obj.description AS description,
        room_obj.status AS roomStatus

    FROM doctor_medical_center_room_schedule dmr
    JOIN medicle_center mc ON dmr.medical_center_id = mc.medicle_center_id
    JOIN JSON_TABLE(
        mc.channeling_rooms,
        '$[*]' COLUMNS (
            id BIGINT PATH '$.id',
            medicalCenterId BIGINT PATH '$.medicalCenterId',
            roomName VARCHAR(255) PATH '$.roomName',
            description VARCHAR(255) PATH '$.description',
            status VARCHAR(255) PATH '$.status'
        )
    ) AS room_obj ON room_obj.id = dmr.channeling_room_id
    WHERE dmr.doctor_id = :doctorId AND dmr.status = 'Active'
""", nativeQuery = true)
    List<DoctorScheduleProjection> getDoctorScheduleWithRoomDetails(@Param("doctorId") Long doctorId);

    @Query(value = """
    SELECT
        dmr.doctor_medical_center_room_schedule_id AS scheduleId,
        dmr.day_of_week AS dayOfWeek,
        dmr.start_time AS startTime,
        dmr.end_time AS endTime,
        dmr.status AS status,

        mc.medicle_center_id AS medicalCenterId,
        mc.center_name AS centerName,
        mc.address AS address,

        room_obj.id AS id,
        room_obj.medicalCenterId AS medicalCenterIdRoom,
        room_obj.roomName AS roomName,
        room_obj.description AS description,
        room_obj.status AS roomStatus

    FROM doctor_medical_center_room_schedule dmr
    JOIN medicle_center mc ON dmr.medical_center_id = mc.medicle_center_id
    JOIN JSON_TABLE(
        mc.channeling_rooms,
        '$[*]' COLUMNS (
            id BIGINT PATH '$.id',
            medicalCenterId BIGINT PATH '$.medicalCenterId',
            roomName VARCHAR(255) PATH '$.roomName',
            description VARCHAR(255) PATH '$.description',
            status VARCHAR(255) PATH '$.status'
        )
    ) AS room_obj ON room_obj.id = dmr.channeling_room_id
    WHERE mc.medicle_center_id = :doctorId AND dmr.status = 'Active'
""", nativeQuery = true)
    List<DoctorScheduleProjection> getDoctorScheduleWithRoomDetailsByMedicalCenterId(@Param("doctorId") Long doctorId);


    @Query(value = "select * from doctor_medical_center_room_schedule where doctor_id =:id and day_of_week=:day and status = 'Active'",nativeQuery = true)
    DoctorMedicalCenterRoomSchedule findByDoctorIdAndDayOfWeekAndStatusActive(@Param("id") long id, @Param("day") String day);


    @Query(value = "select day_of_week from doctor_medical_center_room_schedule where doctor_id = :doctorId and medical_center_id= :medcleCenterId and status = 'Active'",nativeQuery = true)
    List<String> findDayOfWeekByDoctorIdAndMedicalCenterId(@Param("doctorId") Long doctorId, @Param("medcleCenterId") Long medcleCenterId);

    @Query(value = "select start_time from doctor_medical_center_room_schedule where doctor_id=:doctorId and status = 'Active'",nativeQuery = true)
    List<String> findStartTimeByDoctorId(@Param("doctorId") Long doctorId);

    @Query(value = "select * from doctor_medical_center_room_schedule where doctor_id=:doctorId and medical_center_id = :medicalCenterId and day_of_week=:dayOfWeek ",nativeQuery = true)
    DoctorMedicalCenterRoomSchedule findByDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive(@Param("doctorId") Long doctorId, @Param("medicalCenterId") Long medicalCenterId, @Param("dayOfWeek") String dayOfWeek);

    @Query(value = "SELECT " +
            "dmrs.doctor_medical_center_room_schedule_id AS scheduleId, " +
            "dmrs.day_of_week AS dayOfWeek, " +
            "dmrs.start_time AS startTime, " +
            "dmrs.end_time AS endTime, " +
            "dmrs.status AS scheduleStatus, " +
            "d.full_name AS doctorName, " +
            "mc.center_name AS medicalCenterName, " +
            "(SELECT JSON_UNQUOTE(JSON_EXTRACT(room.room_data, '$.roomName')) " +
            " FROM JSON_TABLE( " +
            "   mc.channeling_rooms, " +
            "   '$[*]' COLUMNS( " +
            "       room_id BIGINT PATH '$.id', " +
            "       room_data JSON PATH '$' " +
            "   ) " +
            ") AS room " +
            " WHERE room.room_id = dmrs.channeling_room_id " +
            " LIMIT 1) AS roomName " +
            "FROM doctor_medical_center_room_schedule dmrs " +
            "JOIN doctor d ON dmrs.doctor_id = d.doctor_id " +
            "JOIN medicle_center mc ON dmrs.medical_center_id = mc.medicle_center_id " +
            "WHERE dmrs.status = 'Active'", nativeQuery = true)
    List<ScheduleProjection> findAllActiveDoctorSchedules();



}
