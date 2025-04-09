package com.payrolladvance.advanceservice.service;

import com.payrolladvance.advanceservice.dto.AdvanceRequestDto;
import com.payrolladvance.advanceservice.dto.AdvanceRequestUpdateDto;
import com.payrolladvance.advanceservice.exception.ResourceNotFoundException;
import com.payrolladvance.advanceservice.model.AdvanceRequest;
import com.payrolladvance.advanceservice.repository.AdvanceRequestRepository;
import com.payrolladvance.kafka.common.events.AdvanceRequestEvent;
import com.payrolladvance.kafka.common.events.EventType;
import com.payrolladvance.kafka.common.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the AdvanceRequestService interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvanceRequestServiceImpl implements AdvanceRequestService {
    
    private final AdvanceRequestRepository advanceRequestRepository;
    private final EventPublisher eventPublisher;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AdvanceRequest createAdvanceRequest(AdvanceRequestDto advanceRequestDto) {
        log.info("Creating new advance request for employee ID: {}", advanceRequestDto.getEmployeeId());
        
        AdvanceRequest advanceRequest = new AdvanceRequest();
        advanceRequest.setEmployeeId(advanceRequestDto.getEmployeeId());
        advanceRequest.setAmount(advanceRequestDto.getAmount());
        advanceRequest.setReason(advanceRequestDto.getReason());
        advanceRequest.setRequestedDate(LocalDateTime.now());
        advanceRequest.setStatus("PENDING");
        advanceRequest.setExpectedRepaymentDate(advanceRequestDto.getExpectedRepaymentDate());
        
        AdvanceRequest savedRequest = advanceRequestRepository.save(advanceRequest);
        
        // Publish advance request created event
        eventPublisher.publish(
                "advance-request-events", 
                new AdvanceRequestEvent(
                        savedRequest.getId(),
                        EventType.ADVANCE_REQUEST_CREATED,
                        savedRequest
                )
        );
        
        log.info("Created advance request with ID: {}", savedRequest.getId());
        return savedRequest;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AdvanceRequest getAdvanceRequestById(Long id) {
        log.info("Fetching advance request with ID: {}", id);
        return advanceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advance request not found with ID: " + id));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<AdvanceRequest> getAdvanceRequestsByEmployeeId(Long employeeId) {
        log.info("Fetching advance requests for employee ID: {}", employeeId);
        return advanceRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<AdvanceRequest> getAdvanceRequestsByStatus(String status) {
        log.info("Fetching advance requests with status: {}", status);
        return advanceRequestRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AdvanceRequest updateAdvanceRequestStatus(Long id, AdvanceRequestUpdateDto updateDto) {
        log.info("Updating advance request status for ID: {} to {}", id, updateDto.getStatus());
        
        AdvanceRequest advanceRequest = getAdvanceRequestById(id);
        String oldStatus = advanceRequest.getStatus();
        
        // Update status
        advanceRequest.setStatus(updateDto.getStatus());
        
        // For approval
        if ("APPROVED".equals(updateDto.getStatus())) {
            advanceRequest.setApprovedBy(updateDto.getApprovedBy());
            advanceRequest.setApprovalDate(LocalDateTime.now());
        }
        
        // For rejection
        if ("REJECTED".equals(updateDto.getStatus())) {
            advanceRequest.setRejectionReason(updateDto.getRejectionReason());
        }
        
        AdvanceRequest updatedRequest = advanceRequestRepository.save(advanceRequest);
        
        // Publish appropriate event based on the new status
        EventType eventType;
        switch (updateDto.getStatus()) {
            case "APPROVED" -> eventType = EventType.ADVANCE_REQUEST_APPROVED;
            case "REJECTED" -> eventType = EventType.ADVANCE_REQUEST_REJECTED;
            default -> eventType = EventType.ADVANCE_REQUEST_UPDATED;
        }
        
        eventPublisher.publish(
                "advance-request-events", 
                new AdvanceRequestEvent(
                        updatedRequest.getId(),
                        eventType,
                        updatedRequest
                )
        );
        
        log.info("Updated advance request with ID: {} from status {} to {}", 
                id, oldStatus, updateDto.getStatus());
        
        return updatedRequest;
    }
}