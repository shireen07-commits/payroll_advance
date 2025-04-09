package com.payrolladvance.user.repository;

import com.payrolladvance.user.entity.EmployerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    
    Optional<EmployerProfile> findByUserId(Long userId);
    
    Optional<EmployerProfile> findByCompanyRegistrationNumber(String companyRegistrationNumber);
    
    boolean existsByCompanyRegistrationNumber(String companyRegistrationNumber);
}