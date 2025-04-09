package com.payrolladvance.userservice.repository;

import com.payrolladvance.userservice.model.EmployerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EmployerProfile entity.
 */
@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    
    /**
     * Find an employer profile by user ID.
     *
     * @param userId the user ID to search for
     * @return an Optional containing the employer profile if found, or empty if not found
     */
    Optional<EmployerProfile> findByUserId(Long userId);
    
    /**
     * Find an employer profile by company name.
     *
     * @param companyName the company name to search for
     * @return an Optional containing the employer profile if found, or empty if not found
     */
    Optional<EmployerProfile> findByCompanyName(String companyName);
}