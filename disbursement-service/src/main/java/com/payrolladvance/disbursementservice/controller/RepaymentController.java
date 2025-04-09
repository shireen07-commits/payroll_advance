package com.payrolladvance.disbursementservice.controller;

import com.payrolladvance.disbursementservice.dto.RepaymentDto;
import com.payrolladvance.disbursementservice.model.Repayment;
import com.payrolladvance.disbursementservice.service.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for repayment endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
public class RepaymentController {
    
    private final RepaymentService repaymentService;
    
    /**
     * Creates a new repayment.
     *
     * @param repaymentDto the repayment data
     * @return the created repayment
     */
    @PostMapping
    public ResponseEntity<Repayment> createRepayment(@Valid @RequestBody RepaymentDto repaymentDto) {
        log.info("Received request to create repayment for disbursement ID: {}", repaymentDto.getDisbursementId());
        Repayment createdRepayment = repaymentService.createRepayment(repaymentDto);
        return new ResponseEntity<>(createdRepayment, HttpStatus.CREATED);
    }
    
    /**
     * Gets a repayment by ID.
     *
     * @param id the repayment ID
     * @return the repayment if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Repayment> getRepaymentById(@PathVariable Long id) {
        log.info("Fetching repayment with ID: {}", id);
        Repayment repayment = repaymentService.getRepaymentById(id);
        return ResponseEntity.ok(repayment);
    }
    
    /**
     * Gets all repayments for a specific disbursement.
     *
     * @param disbursementId the disbursement ID
     * @return a list of repayments
     */
    @GetMapping("/disbursement/{disbursementId}")
    public ResponseEntity<List<Repayment>> getRepaymentsByDisbursementId(@PathVariable Long disbursementId) {
        log.info("Fetching repayments for disbursement ID: {}", disbursementId);
        List<Repayment> repayments = repaymentService.getRepaymentsByDisbursementId(disbursementId);
        return ResponseEntity.ok(repayments);
    }
    
    /**
     * Gets all repayments for a specific employee.
     *
     * @param employeeId the employee ID
     * @return a list of repayments
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Repayment>> getRepaymentsByEmployeeId(@PathVariable Long employeeId) {
        log.info("Fetching repayments for employee ID: {}", employeeId);
        List<Repayment> repayments = repaymentService.getRepaymentsByEmployeeId(employeeId);
        return ResponseEntity.ok(repayments);
    }
    
    /**
     * Updates a repayment's status.
     *
     * @param id the repayment ID
     * @param statusMap the status update data
     * @return the updated repayment
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Repayment> updateRepaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> statusMap) {
        
        if (!statusMap.containsKey("status")) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Updating repayment with ID: {} to status: {}", id, statusMap.get("status"));
        Repayment updatedRepayment = repaymentService.updateRepaymentStatus(id, statusMap.get("status"));
        return ResponseEntity.ok(updatedRepayment);
    }
    
    /**
     * Processes a repayment.
     *
     * @param id the repayment ID
     * @return the processed repayment
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<Repayment> processRepayment(@PathVariable Long id) {
        log.info("Processing repayment with ID: {}", id);
        Repayment processedRepayment = repaymentService.processRepayment(id);
        return ResponseEntity.ok(processedRepayment);
    }
}