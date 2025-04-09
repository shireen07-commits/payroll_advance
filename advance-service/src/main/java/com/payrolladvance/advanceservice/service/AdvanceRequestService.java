package com.payrolladvance.advanceservice.service;

import com.payrolladvance.advanceservice.dto.AdvanceRequestDto;
import com.payrolladvance.advanceservice.dto.AdvanceRequestUpdateDto;
import com.payrolladvance.advanceservice.model.AdvanceRequest;

import java.util.List;

/**
 * Service interface for advance request operations.
 */
public interface AdvanceRequestService {
    
    /**
     * Creates a new advance request.
     *
     * @param advanceRequestDto the advance request data
     * @return the created advance request
     */
    AdvanceRequest createAdvanceRequest(AdvanceRequestDto advanceRequestDto);
    
    /**
     * Gets an advance request by ID.
     *
     * @param id the advance request ID
     * @return the advance request if found
     */
    AdvanceRequest getAdvanceRequestById(Long id);
    
    /**
     * Gets all advance requests for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of advance requests
     */
    List<AdvanceRequest> getAdvanceRequestsByEmployeeId(Long employeeId);
    
    /**
     * Gets all advance requests with a specific status.
     *
     * @param status the status to filter by
     * @return a list of advance requests
     */
    List<AdvanceRequest> getAdvanceRequestsByStatus(String status);
    
    /**
     * Updates an advance request status.
     *
     * @param id        the advance request ID
     * @param updateDto the update data
     * @return the updated advance request
     */
    AdvanceRequest updateAdvanceRequestStatus(Long id, AdvanceRequestUpdateDto updateDto);
}