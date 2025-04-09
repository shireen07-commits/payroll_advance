package com.payrolladvance.disbursementservice.controller;

import com.payrolladvance.disbursementservice.dto.DisbursementDto;
import com.payrolladvance.disbursementservice.model.Disbursement;
import com.payrolladvance.disbursementservice.service.DisbursementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for disbursement endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/disbursements")
@RequiredArgsConstructor
public class DisbursementController {
    
    private final DisbursementService disbursementService;
    
    /**
     * Creates a new disbursement.
     *
     * @param disbursementDto the disbursement data
     * @return the created disbursement
     */
    @PostMapping
    public ResponseEntity<Disbursement> createDisbursement(@Valid @RequestBody DisbursementDto disbursementDto) {
        log.info("Received request to create disbursement for advance request ID: {}", disbursementDto.getAdvanceRequestId());
        Disbursement createdDisbursement = disbursementService.createDisbursement(disbursementDto);
        return new ResponseEntity<>(createdDisbursement, HttpStatus.CREATED);
    }
    
    /**
     * Gets a disbursement by ID.
     *
     * @param id the disbursement ID
     * @return the disbursement if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Disbursement> getDisbursementById(@PathVariable Long id) {
        log.info("Fetching disbursement with ID: {}", id);
        Disbursement disbursement = disbursementService.getDisbursementById(id);
        return ResponseEntity.ok(disbursement);
    }
    
    /**
     * Gets a disbursement by advance request ID.
     *
     * @param advanceRequestId the advance request ID
     * @return the disbursement if found
     */
    @GetMapping("/advance-request/{advanceRequestId}")
    public ResponseEntity<Disbursement> getDisbursementByAdvanceRequestId(@PathVariable Long advanceRequestId) {
        log.info("Fetching disbursement for advance request ID: {}", advanceRequestId);
        Disbursement disbursement = disbursementService.getDisbursementByAdvanceRequestId(advanceRequestId);
        return ResponseEntity.ok(disbursement);
    }
    
    /**
     * Gets all disbursements for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of disbursements
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Disbursement>> getDisbursementsByEmployeeId(@PathVariable Long employeeId) {
        log.info("Fetching disbursements for employee ID: {}", employeeId);
        List<Disbursement> disbursements = disbursementService.getDisbursementsByEmployeeId(employeeId);
        return ResponseEntity.ok(disbursements);
    }
    
    /**
     * Updates a disbursement's status.
     *
     * @param id the disbursement ID
     * @param statusMap the status update data
     * @return the updated disbursement
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Disbursement> updateDisbursementStatus(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> statusMap) {
        
        if (!statusMap.containsKey("status")) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Updating disbursement with ID: {} to status: {}", id, statusMap.get("status"));
        Disbursement updatedDisbursement = disbursementService.updateDisbursementStatus(id, statusMap.get("status"));
        return ResponseEntity.ok(updatedDisbursement);
    }
    
    /**
     * Processes a disbursement payment.
     *
     * @param id the disbursement ID
     * @return the processed disbursement
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<Disbursement> processDisbursement(@PathVariable Long id) {
        log.info("Processing disbursement with ID: {}", id);
        Disbursement processedDisbursement = disbursementService.processDisbursement(id);
        return ResponseEntity.ok(processedDisbursement);
    }
}