package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Doctor;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.UserRoles;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.DoctorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor,Long> {

    @Query(value = "select * from doctor where email = :email and status ='Active'",nativeQuery = true)
    Doctor findByEmail(@Param("email") String email);

    @Query(value = "select * from doctor where doctor_id = :id and status ='Active'",nativeQuery = true)
    Doctor findByIdAndStatus(@Param("id") Long id);

    @Query(value = "select count(*) from doctor where status ='Active'",nativeQuery = true)
    long countActiveDoctors();

    @Query(value = """
        SELECT
            d.doctor_id AS id,
            d.uniq_Id AS uniqId,
            d.full_name AS fullName,
            d.gender AS gender,
            d.contact AS contact,
            d.address1 AS address1,
            d.address2 AS address2,
            d.nic AS nic,
            d.email AS email,
            d.password AS password,
            d.medical_Registration_No AS medicalRegistrationNo,
            d.years_Of_Experience AS yearsOfExperience,
            d.hospital_Affiliation AS hospitalAffiliation,
            q.qualification_name AS qualificationName,
            s.specialization_name AS specialization,
            d.status AS status,
            d.doctor_fee AS doctorFee,
            3 AS roleId
        FROM doctor d
                 JOIN qualification q ON d.qualification_Id = q.qualification_id
                 JOIN specialization s ON d.specialization_Id = s.specialization_id
        WHERE d.status = 'Active' order by d.doctor_id desc;    
        """, nativeQuery = true)
    List<DoctorProjection> findAllActiveDoctors();

    @Query(value = """
        SELECT
            d.doctor_id AS id,
            d.uniq_Id AS uniqId,
            d.full_name AS fullName,
            d.gender AS gender,
            d.contact AS contact,
            d.address1 AS address1,
            d.address2 AS address2,
            d.nic AS nic,
            d.email AS email,
            d.password AS password,
            d.medical_Registration_No AS medicalRegistrationNo,
            d.years_Of_Experience AS yearsOfExperience,
            d.hospital_Affiliation AS hospitalAffiliation,
            q.qualification_name AS qualificationName,
            s.specialization_name AS specialization,
            d.status AS status,
            d.doctor_fee AS doctorFee,
            3 AS roleId
        FROM doctor d
                 JOIN qualification q ON d.qualification_Id = q.qualification_id
                 JOIN specialization s ON d.specialization_Id = s.specialization_id
        WHERE email= :email and d.status = 'Active' order by d.doctor_id desc;    
        """, nativeQuery = true)
    DoctorProjection findAllActiveDoctorsByEmail(@Param("email") String email);

    @Query(value = "select patient_count from doctor where doctor_id = :id and status = 'Active'", nativeQuery = true)
    int findPatientCountByDoctorId(@Param("id") long id);

/*    @Query(value = "SELECT DISTINCT d.* FROM doctor d " +
            "JOIN doctor_medical_center_room_schedule dmcrs ON d.doctor_id = dmcrs.doctor_id " +
            "WHERE dmcrs.medical_center_id = :medicalCenterId " +
            "AND d.status = 'Active' AND dmcrs.status = 'Active'", nativeQuery = true)*/

    @Query(value = "SELECT DISTINCT d.* FROM doctor d JOIN doctor_medical_center_room_schedule dmcrs\n" +
            "    ON d.doctor_id = dmcrs.doctor_id\n" +
            "                    WHERE dmcrs.medical_center_id = :medicalCenterId\n" +
            "                      AND d.specialization_id = :specializationId\n" +
            "                      AND d.status = 'Active' AND dmcrs.status = 'Active'",nativeQuery = true)
    List<Doctor> findDoctorsByMedicalCenterId(@Param("medicalCenterId") Long medicalCenterId,@Param("specializationId") Long specializationId);


    @Query(value = "SELECT COUNT(*) AS doctor_count FROM doctor WHERE status ='Active'", nativeQuery = true)
    int countDoctorsByStatusActive();

}
