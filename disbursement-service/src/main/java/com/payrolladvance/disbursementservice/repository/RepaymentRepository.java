package com.payrolladvance.disbursementservice.repository;

import com.payrolladvance.disbursementservice.model.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Repayment entity.
 */
@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    
    /**
     * Finds all repayments for a specific disbursement.
     *
     * @param disbursementId the disbursement ID
     * @return a list of repayments
     */
    List<Repayment> findByDisbursementIdOrderByCreatedAtDesc(Long disbursementId);
    
    /**
     * Finds all repayments for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of repayments
     */
    List<Repayment> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    /**
     * Finds all repayments with a specific status.
     *
     * @param status the status to filter by
     * @return a list of repayments
     */
    List<Repayment> findByStatusOrderByCreatedAtDesc(String status);
}