package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Qualification;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationRepo extends JpaRepository<Qualification,Long> {

    @Query(value = "select *  from qualification where qualification_id = :id and status = 'Active'",nativeQuery = true)
    Qualification findQualificationById(@Param("id") Long id);

    @Query(value = "select *  from qualification where qualification_name = :qualification and status = 'Active'",nativeQuery = true)
    Qualification findQualificationByName(@Param("qualification") String qualification);


}
