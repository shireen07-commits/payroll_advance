package com.payrolladvance.user.repository;

import com.payrolladvance.user.entity.EmployeeProfile;
import com.payrolladvance.user.entity.EmployerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    
    Optional<EmployeeProfile> findByUserId(Long userId);
    
    Optional<EmployeeProfile> findByEmployeeId(String employeeId);
    
    List<EmployeeProfile> findByEmployer(EmployerProfile employer);
    
    List<EmployeeProfile> findByEmployerId(Long employerId);
    
    boolean existsByEmployeeId(String employeeId);
}