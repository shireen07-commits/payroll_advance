package com.payrolladvance.advanceservice.controller;

import com.payrolladvance.advanceservice.dto.AdvanceRequestDto;
import com.payrolladvance.advanceservice.dto.AdvanceRequestUpdateDto;
import com.payrolladvance.advanceservice.model.AdvanceRequest;
import com.payrolladvance.advanceservice.service.AdvanceRequestService;
import com.payrolladvance.advanceservice.service.EligibilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for advance request operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/advance-requests")
@RequiredArgsConstructor
public class AdvanceRequestController {
    
    private final AdvanceRequestService advanceRequestService;
    private final EligibilityService eligibilityService;
    
    /**
     * Creates a new advance request.
     *
     * @param advanceRequestDto the advance request data
     * @return the created advance request
     */
    @PostMapping
    public ResponseEntity<?> createAdvanceRequest(@Valid @RequestBody AdvanceRequestDto advanceRequestDto) {
        log.info("Request to create advance request for employee ID: {}", advanceRequestDto.getEmployeeId());
        
        // Check eligibility
        boolean isEligible = eligibilityService.isEligibleForAdvance(
                advanceRequestDto.getEmployeeId(), 
                advanceRequestDto.getAmount()
        );
        
        if (!isEligible) {
            log.warn("Employee ID: {} is not eligible for requested advance amount: {}", 
                    advanceRequestDto.getEmployeeId(), advanceRequestDto.getAmount());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Not eligible for requested advance amount",
                            "maxEligibleAmount", eligibilityService.getMaxEligibleAmount(advanceRequestDto.getEmployeeId())
                    ));
        }
        
        AdvanceRequest createdRequest = advanceRequestService.createAdvanceRequest(advanceRequestDto);
        log.info("Created advance request with ID: {}", createdRequest.getId());
        
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }
    
    /**
     * Gets an advance request by ID.
     *
     * @param id the advance request ID
     * @return the advance request if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvanceRequest> getAdvanceRequestById(@PathVariable Long id) {
        log.info("Request to get advance request with ID: {}", id);
        
        AdvanceRequest advanceRequest = advanceRequestService.getAdvanceRequestById(id);
        return ResponseEntity.ok(advanceRequest);
    }
    
    /**
     * Gets all advance requests for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of advance requests
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AdvanceRequest>> getAdvanceRequestsByEmployeeId(@PathVariable Long employeeId) {
        log.info("Request to get advance requests for employee ID: {}", employeeId);
        
        List<AdvanceRequest> advanceRequests = advanceRequestService.getAdvanceRequestsByEmployeeId(employeeId);
        return ResponseEntity.ok(advanceRequests);
    }
    
    /**
     * Gets all advance requests with a specific status.
     *
     * @param status the status to filter by
     * @return a list of advance requests
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AdvanceRequest>> getAdvanceRequestsByStatus(@PathVariable String status) {
        log.info("Request to get advance requests with status: {}", status);
        
        List<AdvanceRequest> advanceRequests = advanceRequestService.getAdvanceRequestsByStatus(status);
        return ResponseEntity.ok(advanceRequests);
    }
    
    /**
     * Updates an advance request status.
     *
     * @param id        the advance request ID
     * @param updateDto the update data
     * @return the updated advance request
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AdvanceRequest> updateAdvanceRequestStatus(
            @PathVariable Long id, 
            @Valid @RequestBody AdvanceRequestUpdateDto updateDto
    ) {
        log.info("Request to update advance request status for ID: {} to {}", id, updateDto.getStatus());
        
        AdvanceRequest updatedRequest = advanceRequestService.updateAdvanceRequestStatus(id, updateDto);
        return ResponseEntity.ok(updatedRequest);
    }
    
    /**
     * Gets the maximum eligible advance amount for an employee.
     *
     * @param employeeId the employee ID
     * @return the maximum eligible amount
     */
    @GetMapping("/eligibility/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeEligibility(@PathVariable Long employeeId) {
        log.info("Request to get eligibility for employee ID: {}", employeeId);
        
        BigDecimal maxEligibleAmount = eligibilityService.getMaxEligibleAmount(employeeId);
        
        return ResponseEntity.ok(Map.of(
                "employeeId", employeeId,
                "isEligible", maxEligibleAmount.compareTo(BigDecimal.ZERO) > 0,
                "maxEligibleAmount", maxEligibleAmount
        ));
    }
}