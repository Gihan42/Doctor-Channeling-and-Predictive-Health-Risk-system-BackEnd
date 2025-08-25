package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Appointment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Payment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsForDashBoardProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.TodayAppointmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment,Long> {

    @Query(value = "select * from appointment where id = :id and appointment_status = 'Pending'", nativeQuery = true)
    Appointment findByIdAndStatus(@Param("id")long id);

    @Query(
            value = "SELECT COUNT(*) FROM appointment " +
                    "WHERE doctor_id = :doctorId " +
                    "AND shedule_id = :sheduleId " +
                    "AND DATE(appointment_date) = DATE(:appointmentDate)",
            nativeQuery = true
    )
    int countByDoctorIdAndSheduleIdAndAppointmentDate(
            @Param("doctorId") long doctorId,
            @Param("sheduleId") long sheduleId,
            @Param("appointmentDate") Date appointmentDate
    );

    @Query(value = "select * from appointment where appointment_date = :date and appointment_status = 'Pending'",nativeQuery = true)
    List<Appointment> findByAppointmentDateAndStatus(@Param("date") Date date);

    @Query(value = "select * from appointment where doctor_id = :id and appointment_date = :date and appointment_status = 'Pending' and medicle_center_id = :medicle_center_id and channeling_room_id = :channeling_room_id",nativeQuery = true)
    List<Appointment> findAllByAppointmentDateAndStatus(
            @Param("id") long id,
            @Param("date") Date date,
            @Param("medicle_center_id") long medicleCenterId,
            @Param("channeling_room_id") long channelingRoomId
    );

    @Query(value="select * from appointment where appointment_eend_time = :end_time and appointment_date = :date and appointment_status = 'Pending'",nativeQuery = true)
    Appointment findByAppointmentEndTimeAndDateAndStatus(
            @Param("end_time") Date endTime,
            @Param("date") Date date
    );

    @Query(value = "select * from appointment where appointment_date = :date and appointment_status = 'Pending'",nativeQuery = true)
    List<Appointment> findByAppointmentDate(
            @Param("date") Date date
    );

    @Query(value = "select channel_number from appointment where appointment_date = :date and medicle_center_id= :centerId and  room_id = :roomId and appointment_status = 'Pending'  limit 1",nativeQuery = true)
    String getCurrentChannelNumberByMedicineCenterIdAndRoomId(@Param("date")Date date,
                                                              @Param("centerId") long centerId,
                                                              @Param("roomId")  long roomId);

    @Query(value = "select * from appointment where patient_id = :patientId",nativeQuery = true)
    List<Appointment> findByPatientId(@Param("patientId") long patientId);

    @Query(value = """
    SELECT
        a.id AS id,
        a.patient_id AS patientId,
        a.patient_name AS patientName,
        a.doctor_id AS doctorId,
        a.medicle_center_id AS medicleCenterId,
        a.room_id AS roomId,
        a.shedule_id AS sheduleId,
        a.appointment_date AS appointmentDate,
        a.appointment_time AS appointmentTime,
        a.appointment_eend_time AS appointmentEndTime,
        a.channel_number AS channelNumber,
        a.booking_date AS bookingDate,
        a.appointment_status AS appointmentStatus,

        d.full_name AS doctorName,
        d.gender AS doctorGender,
        d.contact AS doctorContact,
        d.email AS doctorEmail,
        d.medical_Registration_No AS medicalRegistrationNo,
        d.years_Of_Experience AS yearsOfExperience,

        s.specialization_name AS specializationName,

        mc.center_name AS medicalCenterName,
        mc.registration_number AS registrationNumber,
        mc.contact1 AS medicalCenterContact,
        mc.email AS medicalCenterEmail,
        mc.distric AS distric,
        mc.open_time AS openTime,
        mc.close_time AS closeTime,
        mc.channeling_fee AS channelingFee,

        mct.center_type AS medicalCenterType

    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.doctor_id
    JOIN specialization s ON d.specialization_id = s.specialization_id
    JOIN medicle_center mc ON a.medicle_center_id = mc.medicle_center_id
    JOIN medicle_center_type mct ON mc.center_type_id = mct.medicle_center_type_id
    WHERE a.patient_id = :patientId
""", nativeQuery = true)
    List<AppointmentProjection> findAllAppointmentDetailsByPatientId(@Param("patientId") long patientId);

    @Query(value = """
        SELECT 
            d.full_name AS doctorName,
            a.channel_number AS channelingNumber,
            a.room_id AS channelingRoomId,
            a.appointment_date AS appointmentDate,
            a.appointment_time AS appointmentTime,
            mc.center_name AS medicalCenterName,
            a.appointment_status AS appointmentStatus
        FROM 
            appointment a
        JOIN 
            doctor d ON a.doctor_id = d.doctor_id
        JOIN 
            medicle_center mc ON a.medicle_center_id = mc.medicle_center_id
        WHERE 
            a.patient_id = :patientId
        """, nativeQuery = true)
    List<AppointmentDetailsForDashBoardProjection> findAppointmentDetailsByPatientId(@Param("patientId") Long patientId);


    @Query(value ="SELECT COUNT(*) AS appointment_count FROM appointment WHERE appointment_status ='Pending'",nativeQuery = true)
    int countPendingAppointments();

    @Query(value = """
        SELECT 
            d.full_name AS doctorName,
            a.appointment_time AS appointmentTime,
            mc.center_name AS medicalCenterName,
            s.start_time AS startTime,
            s.end_time AS endTime
        FROM 
            appointment a
        JOIN 
            doctor d ON a.doctor_id = d.doctor_id
        JOIN 
            medicle_center mc ON a.medicle_center_id = mc.medicle_center_id
        JOIN 
            doctor_medical_center_room_schedule s 
            ON a.doctor_id = s.doctor_id 
            AND a.medicle_center_id = s.medical_center_id 
            AND a.room_id = s.channeling_room_id
        WHERE 
            a.appointment_status = 'Pending'
            AND DATE(a.appointment_date) = CURDATE()
        """, nativeQuery = true)
    List<TodayAppointmentProjection> getTodayPendingAppointments();
}
