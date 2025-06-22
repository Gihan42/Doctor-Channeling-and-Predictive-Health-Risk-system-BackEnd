package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MedicalCenterTypeRepo extends JpaRepository<MedicalCenterType, Long> {

    @Query(value = "select * from medicle_center_type where center_type = :center_type and status = 'Active'",nativeQuery = true)
    MedicalCenterType findByCenterType(@Param("center_type") String center_type);


}
