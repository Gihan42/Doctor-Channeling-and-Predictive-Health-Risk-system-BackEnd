package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.UserRoles;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.MedicalCenterSpecializationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecializationRepo extends JpaRepository<Specialization,Long> {
    @Query(value = "select *  from specialization where specialization_id = :id and status = 'Active'",nativeQuery = true)
    Specialization findByIdAndStatusActive(@Param("id") Long id);

    @Query(value = "select *  from specialization where specialization_name = :specialization and status = 'Active'",nativeQuery = true)
    Specialization findBySpecializationAndStatusActive(@Param("specialization") String specialization);

    @Query(value ="select * from specialization where status = 'Active' order by specialization_id desc",nativeQuery = true)
    List<Specialization> getAllActiveSpecializations();


    @Query(value = """
        SELECT DISTINCT
            s.specialization_name AS specializationName,
            mc.medicle_center_id AS medicleCenterId,
            mc.center_name AS centerName,
            mc.contact1 AS contact1,
            mc.contact2 AS contact2,
            mc.email AS email,
            mc.address AS address,
            mc.distric AS distric,
            mc.open_time AS openTime,
            mc.close_time AS closeTime,
            mc.channeling_fee AS channelingFee,
            mct.center_type AS centerType
        FROM specialization s
        JOIN doctor d ON d.specialization_id = s.specialization_id
        JOIN doctor_medical_center_room_schedule dmcrs ON dmcrs.doctor_id = d.doctor_id
        JOIN medicle_center mc ON mc.medicle_center_id = dmcrs.medical_center_id
        JOIN medicle_center_type mct ON mct.medicle_center_type_id = mc.center_type_id
        WHERE s.specialization_name = :specializationName
        """, nativeQuery = true)
    List<MedicalCenterSpecializationProjection> findMedicalCentersBySpecialization(@Param("specializationName") String specializationName);

}
