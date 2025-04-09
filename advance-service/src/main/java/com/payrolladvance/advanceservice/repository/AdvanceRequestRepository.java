package com.payrolladvance.advanceservice.repository;

import com.payrolladvance.advanceservice.model.AdvanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for AdvanceRequest entity.
 */
@Repository
public interface AdvanceRequestRepository extends JpaRepository<AdvanceRequest, Long> {
    
    /**
     * Finds all advance requests for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of advance requests
     */
    List<AdvanceRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    /**
     * Finds all advance requests with a specific status.
     *
     * @param status the status to filter by
     * @return a list of advance requests
     */
    List<AdvanceRequest> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * Finds all advance requests approved by a specific user.
     *
     * @param approvedBy the approver ID
     * @return a list of advance requests
     */
    List<AdvanceRequest> findByApprovedByOrderByApprovalDateDesc(Long approvedBy);
}