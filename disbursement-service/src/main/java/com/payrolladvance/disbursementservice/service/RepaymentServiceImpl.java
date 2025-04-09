package com.payrolladvance.disbursementservice.service;

import com.payrolladvance.disbursementservice.dto.RepaymentDto;
import com.payrolladvance.disbursementservice.exception.ResourceNotFoundException;
import com.payrolladvance.disbursementservice.model.Repayment;
import com.payrolladvance.disbursementservice.repository.RepaymentRepository;
import com.payrolladvance.kafka.common.events.EventType;
import com.payrolladvance.kafka.common.events.RepaymentEvent;
import com.payrolladvance.kafka.common.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the RepaymentService interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {
    
    private final RepaymentRepository repaymentRepository;
    private final EventPublisher eventPublisher;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Repayment createRepayment(RepaymentDto repaymentDto) {
        log.info("Creating new repayment for disbursement ID: {}", repaymentDto.getDisbursementId());
        
        Repayment repayment = new Repayment();
        repayment.setDisbursementId(repaymentDto.getDisbursementId());
        repayment.setEmployeeId(repaymentDto.getEmployeeId());
        repayment.setAmount(repaymentDto.getAmount());
        repayment.setPaymentMethod(repaymentDto.getPaymentMethod());
        repayment.setPaymentDate(repaymentDto.getPaymentDate() != null ? repaymentDto.getPaymentDate() : LocalDateTime.now());
        
        // Set initial status
        repayment.setStatus("PENDING");
        
        Repayment savedRepayment = repaymentRepository.save(repayment);
        
        // Publish repayment created event
        eventPublisher.publish(
                "repayment-events", 
                new RepaymentEvent(
                        savedRepayment.getId(),
                        EventType.REPAYMENT_CREATED,
                        savedRepayment
                )
        );
        
        log.info("Created repayment with ID: {}", savedRepayment.getId());
        return savedRepayment;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Repayment getRepaymentById(Long id) {
        log.info("Fetching repayment with ID: {}", id);
        return repaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment not found with ID: " + id));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Repayment> getRepaymentsByDisbursementId(Long disbursementId) {
        log.info("Fetching repayments for disbursement ID: {}", disbursementId);
        return repaymentRepository.findByDisbursementIdOrderByCreatedAtDesc(disbursementId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Repayment> getRepaymentsByEmployeeId(Long employeeId) {
        log.info("Fetching repayments for employee ID: {}", employeeId);
        return repaymentRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Repayment updateRepaymentStatus(Long id, String status) {
        log.info("Updating repayment status for ID: {} to {}", id, status);
        
        Repayment repayment = getRepaymentById(id);
        repayment.setStatus(status);
        Repayment updatedRepayment = repaymentRepository.save(repayment);
        
        // Publish repayment updated event
        eventPublisher.publish(
                "repayment-events", 
                new RepaymentEvent(
                        updatedRepayment.getId(),
                        EventType.REPAYMENT_UPDATED,
                        updatedRepayment
                )
        );
        
        return updatedRepayment;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Repayment processRepayment(Long id) {
        log.info("Processing repayment with ID: {}", id);
        
        Repayment repayment = getRepaymentById(id);
        
        // Don't process if not in PENDING state
        if (!"PENDING".equals(repayment.getStatus())) {
            log.warn("Cannot process repayment that is not in PENDING state. Current state: {}", repayment.getStatus());
            return repayment;
        }
        
        try {
            // Update status to PROCESSING
            repayment.setStatus("PROCESSING");
            repaymentRepository.save(repayment);
            
            // Simulate payment processing
            // In a real system, we would integrate with a payment provider here
            String transactionReference = UUID.randomUUID().toString();
            repayment.setTransactionReference(transactionReference);
            
            // Update status to COMPLETED
            repayment.setStatus("COMPLETED");
            Repayment completedRepayment = repaymentRepository.save(repayment);
            
            // Publish repayment completed event
            eventPublisher.publish(
                    "repayment-events", 
                    new RepaymentEvent(
                            completedRepayment.getId(),
                            EventType.REPAYMENT_COMPLETED,
                            completedRepayment
                    )
            );
            
            log.info("Successfully processed repayment with ID: {}", id);
            return completedRepayment;
        } catch (Exception e) {
            log.error("Error processing repayment with ID: {}", id, e);
            
            // Update status to FAILED
            repayment.setStatus("FAILED");
            Repayment failedRepayment = repaymentRepository.save(repayment);
            
            // Publish repayment failed event
            eventPublisher.publish(
                    "repayment-events", 
                    new RepaymentEvent(
                            failedRepayment.getId(),
                            EventType.REPAYMENT_FAILED,
                            failedRepayment
                    )
            );
            
            return failedRepayment;
        }
    }
}