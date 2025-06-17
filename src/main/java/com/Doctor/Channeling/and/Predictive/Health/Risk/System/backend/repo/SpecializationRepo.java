package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.UserRoles;
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
}
