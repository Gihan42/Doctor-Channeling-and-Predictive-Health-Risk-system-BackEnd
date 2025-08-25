package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;


import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Payment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PaymentSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment,Long> {

    @Query(value = """
    SELECT
        a.id AS appointmentId,
        a.patient_id AS patientId,
        a.patient_name AS patientName,
        a.doctor_id AS doctorId,
        d.full_name AS doctorName,
        d.gender AS gender,
        d.contact AS doctorContact,
        d.email AS doctorEmail,
        s.specialization_name AS specializationName,
        a.medicle_center_id AS medicleCenterId,
        mc.center_name AS medicalCenterName,
        mc.address AS medicalCenterAddress,
        mc.contact1 AS medicalCenterContact1,
        mc.contact2 AS medicalCenterContact2,
        mc.email AS medicalCenterEmail,
        mc.distric AS distric,
        a.room_id AS roomId,
        a.shedule_id AS sheduleId,
        a.appointment_date AS appointmentDate,
        a.appointment_time AS appointmentTime,
        a.channel_number AS channelNumber,
        a.booking_date AS bookingDate,
        a.appointment_status AS appointmentStatus,
        a.payment_id AS paymentId,
        p.amount AS paymentAmount,
        p.payment_date AS paymentDate
    FROM appointment a
    LEFT JOIN payment p ON p.id = a.payment_id
    JOIN doctor d ON a.doctor_id = d.doctor_id
    JOIN specialization s ON d.specialization_Id = s.specialization_id
    JOIN medicle_center mc ON a.medicle_center_id = mc.medicle_center_id
    WHERE a.patient_id = :patientId
    ORDER BY a.appointment_date DESC, a.appointment_time DESC
    """, nativeQuery = true)
    List<AppointmentDetailsProjection> getAppointmentsWithDetailsByPatientId(@Param("patientId") Long patientId);


    @Query(value = "SELECT\n" +
            "    p.id AS paymentId,\n" +
            "    pa.patient_id AS patientId,\n" +
            "    pa.full_name AS patientName,\n" +
            "    mc.medicle_center_id AS medicalCenterId,\n" +
            "    mc.center_name AS medicalCenterName,\n" +
            "    p.amount AS paidAmount,\n" +
            "    p.payment_date AS paymentDate\n" +
            "FROM payment p\n" +
            "JOIN patient pa ON p.patient_id = pa.patient_id\n" +
            "JOIN medicle_center mc ON p.medical_center_id = mc.medicle_center_id",
            nativeQuery = true)
    List<PaymentSummaryProjection> getPaymentSummary();
}
