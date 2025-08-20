package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Patient;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.PatientReview;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PatientReviewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientReviewRepo extends JpaRepository<PatientReview,Long> {

    @Query(value = "select * from patient_review where patient_review_id = :patient_review_id and status ='Active'", nativeQuery = true)
    PatientReview findByPatientReviewId(@Param("patient_review_id") Long patient_review_id);

    @Query(value = "select count(*) as review_count from patient_review where viewed ='1'",nativeQuery = true)
    int countViewedPatientReview();

    @Query(value = "SELECT\n" +
            "    pr.patient_review_id as patientReviewId,\n" +
            "    pr.comment as comment,\n" +
            "    pr.date as date ,\n" +
            "    pr.viewed as viewed,\n" +
            "    pr.status as status,\n" +
            "    p.full_name AS patientName\n" +
            "FROM\n" +
            "    patient_review pr\n" +
            "        JOIN\n" +
            "    patient p ON pr.patient_id = p.patient_id where pr.viewed ='1' and pr.status ='Active'",
            nativeQuery = true)
    List<PatientReviewProjection> getAllPatientReviewsWithPatientName();


    @Query(value = "SELECT\n" +
            "    pr.patient_review_id as patientReviewId,\n" +
            "    pr.comment as comment,\n" +
            "    pr.date as date ,\n" +
            "    pr.viewed as viewed,\n" +
            "    pr.status as status,\n" +
            "    p.full_name AS patientName\n" +
            "FROM\n" +
            "    patient_review pr\n" +
            "        JOIN\n" +
            "    patient p ON pr.patient_id = p.patient_id where pr.status ='Active' and pr.viewed ='1'",
            nativeQuery = true)
    List<PatientReviewProjection> getAllPatientReviews();

}
