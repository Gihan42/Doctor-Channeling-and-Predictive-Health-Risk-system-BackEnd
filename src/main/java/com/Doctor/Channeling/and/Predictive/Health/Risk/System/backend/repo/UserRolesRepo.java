package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepo extends JpaRepository<UserRoles,Long> {

    @Query(value = "select user_role from user_roles where user_roleid = :user_roleid", nativeQuery = true)
    String findRoleById(@Param("user_roleid") Long  user_roleid);

    @Query(value = "select user_roleid from user_roles where user_role = :role",nativeQuery = true)
    long getRoleIdByRole(@Param("role") String role);
}
