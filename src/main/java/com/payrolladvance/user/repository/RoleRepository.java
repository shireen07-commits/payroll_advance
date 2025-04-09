package com.payrolladvance.user.repository;

import com.payrolladvance.user.entity.Role;
import com.payrolladvance.user.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    Optional<Role> findByName(RoleName name);
}