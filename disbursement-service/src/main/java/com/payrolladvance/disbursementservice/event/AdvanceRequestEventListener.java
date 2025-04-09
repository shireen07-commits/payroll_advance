package com.payrolladvance.disbursementservice.event;

import com.payrolladvance.disbursementservice.dto.DisbursementDto;
import com.payrolladvance.disbursementservice.service.DisbursementService;
import com.payrolladvance.kafka.common.events.AdvanceRequestEvent;
import com.payrolladvance.kafka.common.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Listener for advance request events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvanceRequestEventListener {
    
    private final DisbursementService disbursementService;
    
    /**
     * Listens for advance request events and triggers disbursement when an advance request is approved.
     *
     * @param event the advance request event
     */
    @KafkaListener(topics = "advance-request-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handleAdvanceRequestEvent(AdvanceRequestEvent event) {
        log.info("Received advance request event: {}", event);
        
        if (event.getEventType() == EventType.ADVANCE_REQUEST_APPROVED) {
            log.info("Processing approved advance request ID: {}", event.getEntityId());
            
            // Create disbursement for approved advance request
            try {
                // Extract advance request data from the event payload
                Long advanceRequestId = event.getEntityId();
                Long employeeId = (Long) event.getPayload().get("employeeId");
                BigDecimal amount = new BigDecimal(event.getPayload().get("amount").toString());
                
                // Calculate fee (example: 2% of advance amount)
                BigDecimal feeAmount = amount.multiply(new BigDecimal("0.02"));
                
                // Set expected repayment date (example: 30 days from now)
                LocalDateTime expectedRepaymentDate = LocalDateTime.now().plusDays(30);
                
                // Create disbursement DTO
                DisbursementDto disbursementDto = DisbursementDto.builder()
                        .advanceRequestId(advanceRequestId)
                        .employeeId(employeeId)
                        .amount(amount)
                        .feeAmount(feeAmount)
                        .expectedRepaymentDate(expectedRepaymentDate)
                        .paymentMethod("BANK_TRANSFER") // Default payment method
                        .build();
                
                // Create and process the disbursement
                disbursementService.createDisbursement(disbursementDto);
                log.info("Disbursement created for advance request ID: {}", advanceRequestId);
                
            } catch (Exception e) {
                log.error("Error creating disbursement for advance request ID: {}", event.getEntityId(), e);
            }
        }
    }
}