package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.UserRoles;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PaymentSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<Patient,Long> {
    @Query(value = "select * from patient where email = :email and status='Active'",nativeQuery = true)
    Patient findByPatientEmail(@Param("email") String email);

    @Query(value = "select count(*) from patient where status ='Active'",nativeQuery = true)
    long countPatientStatus();

    @Query(value = "select * from patient where  patient_id = :id  and status='Active'",nativeQuery = true)
    Patient findByPatientId(@Param("id") long id);

    @Query(value = "select * from patient where status = 'Active' order by  patient_id desc",nativeQuery = true)
    List<Patient> getAllActivePatients();

    @Query(value = "select * from patient where email = :email and status = 'Active'",nativeQuery = true)
    Patient getPatientByEmail(@Param("email") String email);



}
