package com.payrolladvance.advanceservice.service;

import java.math.BigDecimal;

/**
 * Service interface for determining advance eligibility.
 */
public interface EligibilityService {
    
    /**
     * Checks if an employee is eligible for an advance of the specified amount.
     *
     * @param employeeId the employee ID
     * @param amount     the requested advance amount
     * @return true if eligible, false otherwise
     */
    boolean isEligibleForAdvance(Long employeeId, BigDecimal amount);
    
    /**
     * Gets the maximum advance amount an employee is eligible for.
     *
     * @param employeeId the employee ID
     * @return the maximum eligible amount
     */
    BigDecimal getMaxEligibleAmount(Long employeeId);
}