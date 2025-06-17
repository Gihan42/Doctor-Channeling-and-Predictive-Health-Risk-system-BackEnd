package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Admin;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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


    @Query(value = """
    SELECT admin_id as id FROM admin WHERE uniq_id = :uniq_id  and status = 'Active'
    UNION
    SELECT doctor_id as  id FROM doctor WHERE uniq_id = :uniq_id and status = 'Active'
    UNION
    SELECT patient_id as id FROM patient WHERE uniq_id = :uniq_id and status = 'Active'
    """, nativeQuery = true)
    long findUserIdByUniqId(@Param("uniq_id") String uniq_id);



    @Query(value = "select * from admin where email = :email and status ='Active'",nativeQuery = true)
    Admin findAdminByEmail(@Param("email") String email);

    @Query(value = "select count(*) from admin where status ='Active'",nativeQuery = true)
    long getActiveAdminCount();

    @Query(value="select * from admin where admin_id = :id and status ='Active'",nativeQuery = true)
    Admin getAdminById(@Param("id") long id);

    @Query(value = "select * from admin where status ='Active' order by  admin_id desc",nativeQuery = true)
    List<Admin> getAllActiveAdmins();

    @Query(value = "select * from admin where email= :email and status ='Active'",nativeQuery = true)
    Admin getAdminByEmail(@Param("email") String email);
}
