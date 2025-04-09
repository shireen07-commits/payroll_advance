package com.payrolladvance.disbursementservice.repository;

import com.payrolladvance.disbursementservice.model.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Disbursement entity.
 */
@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {
    
    /**
     * Finds a disbursement by advance request ID.
     *
     * @param advanceRequestId the advance request ID
     * @return an Optional containing the disbursement if found
     */
    Optional<Disbursement> findByAdvanceRequestId(Long advanceRequestId);
    
    /**
     * Finds all disbursements for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of disbursements
     */
    List<Disbursement> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    /**
     * Finds all disbursements with a specific status.
     *
     * @param status the status to filter by
     * @return a list of disbursements
     */
    List<Disbursement> findByStatusOrderByCreatedAtDesc(String status);
}