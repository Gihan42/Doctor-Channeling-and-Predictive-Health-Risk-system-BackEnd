package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {

    @Query(value = """
    SELECT email, password, role_id as roleId, full_name as fullName , uniq_Id as userId FROM admin WHERE email = :email  and status = 'Active'
    UNION
    SELECT email, password, role_id as roleId, full_name as fullName , uniq_Id as userId FROM doctor WHERE email = :email and status = 'Active'
    UNION
    SELECT email, password, role_id as roleId, full_name as fullName , uniq_Id as userId FROM patient WHERE email = :email and status = 'Active'
    """, nativeQuery = true)
    UserProjection findUserByEmail(@Param("email") String email);




}
