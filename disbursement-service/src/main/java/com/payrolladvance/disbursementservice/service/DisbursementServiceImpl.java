package com.payrolladvance.disbursementservice.service;

import com.payrolladvance.disbursementservice.dto.DisbursementDto;
import com.payrolladvance.disbursementservice.exception.ResourceNotFoundException;
import com.payrolladvance.disbursementservice.model.Disbursement;
import com.payrolladvance.disbursementservice.repository.DisbursementRepository;
import com.payrolladvance.kafka.common.events.DisbursementEvent;
import com.payrolladvance.kafka.common.events.EventType;
import com.payrolladvance.kafka.common.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the DisbursementService interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {
    
    private final DisbursementRepository disbursementRepository;
    private final EventPublisher eventPublisher;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Disbursement createDisbursement(DisbursementDto disbursementDto) {
        log.info("Creating new disbursement for advance request ID: {}", disbursementDto.getAdvanceRequestId());
        
        Disbursement disbursement = new Disbursement();
        disbursement.setAdvanceRequestId(disbursementDto.getAdvanceRequestId());
        disbursement.setEmployeeId(disbursementDto.getEmployeeId());
        disbursement.setAmount(disbursementDto.getAmount());
        disbursement.setPaymentMethod(disbursementDto.getPaymentMethod());
        disbursement.setExpectedRepaymentDate(disbursementDto.getExpectedRepaymentDate());
        disbursement.setFeeAmount(disbursementDto.getFeeAmount() != null ? disbursementDto.getFeeAmount() : BigDecimal.ZERO);
        
        // Calculate total repayment amount
        BigDecimal totalRepaymentAmount = disbursementDto.getAmount().add(
                disbursement.getFeeAmount() != null ? disbursement.getFeeAmount() : BigDecimal.ZERO);
        disbursement.setTotalRepaymentAmount(totalRepaymentAmount);
        
        // Set initial status
        disbursement.setStatus("PENDING");
        
        Disbursement savedDisbursement = disbursementRepository.save(disbursement);
        
        // Publish disbursement created event
        eventPublisher.publish(
                "disbursement-events", 
                new DisbursementEvent(
                        savedDisbursement.getId(),
                        EventType.DISBURSEMENT_CREATED,
                        savedDisbursement
                )
        );
        
        log.info("Created disbursement with ID: {}", savedDisbursement.getId());
        return savedDisbursement;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Disbursement getDisbursementById(Long id) {
        log.info("Fetching disbursement with ID: {}", id);
        return disbursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disbursement not found with ID: " + id));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Disbursement getDisbursementByAdvanceRequestId(Long advanceRequestId) {
        log.info("Fetching disbursement for advance request ID: {}", advanceRequestId);
        return disbursementRepository.findByAdvanceRequestId(advanceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Disbursement not found for advance request ID: " + advanceRequestId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Disbursement> getDisbursementsByEmployeeId(Long employeeId) {
        log.info("Fetching disbursements for employee ID: {}", employeeId);
        return disbursementRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Disbursement updateDisbursementStatus(Long id, String status) {
        log.info("Updating disbursement status for ID: {} to {}", id, status);
        
        Disbursement disbursement = getDisbursementById(id);
        disbursement.setStatus(status);
        Disbursement updatedDisbursement = disbursementRepository.save(disbursement);
        
        // Publish disbursement updated event
        eventPublisher.publish(
                "disbursement-events", 
                new DisbursementEvent(
                        updatedDisbursement.getId(),
                        EventType.DISBURSEMENT_UPDATED,
                        updatedDisbursement
                )
        );
        
        return updatedDisbursement;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Disbursement processDisbursement(Long id) {
        log.info("Processing disbursement with ID: {}", id);
        
        Disbursement disbursement = getDisbursementById(id);
        
        // Don't process if not in PENDING state
        if (!"PENDING".equals(disbursement.getStatus())) {
            log.warn("Cannot process disbursement that is not in PENDING state. Current state: {}", disbursement.getStatus());
            return disbursement;
        }
        
        try {
            // Update status to PROCESSING
            disbursement.setStatus("PROCESSING");
            disbursementRepository.save(disbursement);
            
            // Simulate payment processing
            // In a real system, we would integrate with a payment provider here
            String transactionReference = UUID.randomUUID().toString();
            disbursement.setTransactionReference(transactionReference);
            
            // Update status to COMPLETED
            disbursement.setStatus("COMPLETED");
            Disbursement completedDisbursement = disbursementRepository.save(disbursement);
            
            // Publish disbursement completed event
            eventPublisher.publish(
                    "disbursement-events", 
                    new DisbursementEvent(
                            completedDisbursement.getId(),
                            EventType.DISBURSEMENT_COMPLETED,
                            completedDisbursement
                    )
            );
            
            log.info("Successfully processed disbursement with ID: {}", id);
            return completedDisbursement;
        } catch (Exception e) {
            log.error("Error processing disbursement with ID: {}", id, e);
            
            // Update status to FAILED
            disbursement.setStatus("FAILED");
            Disbursement failedDisbursement = disbursementRepository.save(disbursement);
            
            // Publish disbursement failed event
            eventPublisher.publish(
                    "disbursement-events", 
                    new DisbursementEvent(
                            failedDisbursement.getId(),
                            EventType.DISBURSEMENT_FAILED,
                            failedDisbursement
                    )
            );
            
            return failedDisbursement;
        }
    }
}