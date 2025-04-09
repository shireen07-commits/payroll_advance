package com.payrolladvance.disbursementservice.service;

import com.payrolladvance.disbursementservice.dto.RepaymentDto;
import com.payrolladvance.disbursementservice.model.Repayment;

import java.util.List;

/**
 * Service interface for repayment operations.
 */
public interface RepaymentService {
    
    /**
     * Creates a new repayment.
     *
     * @param repaymentDto the repayment data
     * @return the created repayment
     */
    Repayment createRepayment(RepaymentDto repaymentDto);
    
    /**
     * Gets a repayment by ID.
     *
     * @param id the repayment ID
     * @return the repayment if found
     */
    Repayment getRepaymentById(Long id);
    
    /**
     * Gets all repayments for a specific disbursement.
     *
     * @param disbursementId the disbursement ID
     * @return a list of repayments
     */
    List<Repayment> getRepaymentsByDisbursementId(Long disbursementId);
    
    /**
     * Gets all repayments for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of repayments
     */
    List<Repayment> getRepaymentsByEmployeeId(Long employeeId);
    
    /**
     * Updates a repayment's status.
     *
     * @param id the repayment ID
     * @param status the new status
     * @return the updated repayment
     */
    Repayment updateRepaymentStatus(Long id, String status);
    
    /**
     * Processes a repayment.
     *
     * @param id the repayment ID
     * @return the processed repayment
     */
    Repayment processRepayment(Long id);
}