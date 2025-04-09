package com.payrolladvance.disbursementservice.service;

import com.payrolladvance.disbursementservice.dto.DisbursementDto;
import com.payrolladvance.disbursementservice.model.Disbursement;

import java.util.List;

/**
 * Service interface for disbursement operations.
 */
public interface DisbursementService {
    
    /**
     * Creates a new disbursement.
     *
     * @param disbursementDto the disbursement data
     * @return the created disbursement
     */
    Disbursement createDisbursement(DisbursementDto disbursementDto);
    
    /**
     * Gets a disbursement by ID.
     *
     * @param id the disbursement ID
     * @return the disbursement if found
     */
    Disbursement getDisbursementById(Long id);
    
    /**
     * Gets a disbursement by advance request ID.
     *
     * @param advanceRequestId the advance request ID
     * @return the disbursement if found
     */
    Disbursement getDisbursementByAdvanceRequestId(Long advanceRequestId);
    
    /**
     * Gets all disbursements for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of disbursements
     */
    List<Disbursement> getDisbursementsByEmployeeId(Long employeeId);
    
    /**
     * Updates a disbursement's status.
     *
     * @param id the disbursement ID
     * @param status the new status
     * @return the updated disbursement
     */
    Disbursement updateDisbursementStatus(Long id, String status);
    
    /**
     * Processes a disbursement payment.
     *
     * @param id the disbursement ID
     * @return the processed disbursement
     */
    Disbursement processDisbursement(Long id);
}