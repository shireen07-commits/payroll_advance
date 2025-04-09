package com.payrolladvance.userservice.repository;

import com.payrolladvance.userservice.model.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmployeeProfile entity.
 */
@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    
    /**
     * Find an employee profile by user ID.
     *
     * @param userId the user ID to search for
     * @return an Optional containing the employee profile if found, or empty if not found
     */
    Optional<EmployeeProfile> findByUserId(Long userId);
    
    /**
     * Find all employee profiles for a specific employer.
     *
     * @param employerId the employer ID to search for
     * @return a list of employee profiles for the specified employer
     */
    List<EmployeeProfile> findByEmployerId(Long employerId);
}