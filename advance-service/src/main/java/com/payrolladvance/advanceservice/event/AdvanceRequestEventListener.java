package com.payrolladvance.advanceservice.event;

import com.payrolladvance.advanceservice.model.AdvanceRequest;
import com.payrolladvance.kafka.common.config.KafkaTopics;
import com.payrolladvance.kafka.common.events.advance.AdvanceRequestCreatedEvent;
import com.payrolladvance.kafka.common.events.advance.AdvanceRequestStatusUpdatedEvent;
import com.payrolladvance.kafka.common.events.disbursement.DisbursementCompletedEvent;
import com.payrolladvance.kafka.common.events.user.UserCreatedEvent;
import com.payrolladvance.kafka.common.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Component that listens to and publishes Kafka events related to advance requests.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvanceRequestEventListener {

    private final EventPublisher eventPublisher;
    
    /**
     * Listens to user created events.
     * This allows the advance service to be aware of new users.
     *
     * @param event the user created event
     */
    @KafkaListener(topics = KafkaTopics.USER_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user created event: {}", event);
        // No specific action needed now, but could be used for employee validation
    }
    
    /**
     * Listens to disbursement completed events.
     * This allows the advance service to update advance request status when disbursement is complete.
     *
     * @param event the disbursement completed event
     */
    @KafkaListener(topics = KafkaTopics.DISBURSEMENT_COMPLETED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleDisbursementCompleted(DisbursementCompletedEvent event) {
        log.info("Received disbursement completed event: {}", event);
        // The service would normally update the advance request status to DISBURSED
    }
    
    /**
     * Publishes an advance request created event to Kafka.
     *
     * @param advanceRequest the advance request
     */
    public void publishAdvanceRequestCreatedEvent(AdvanceRequest advanceRequest) {
        log.info("Publishing advance request created event for ID: {}", advanceRequest.getId());
        
        AdvanceRequestCreatedEvent event = new AdvanceRequestCreatedEvent(
                advanceRequest.getId(),
                advanceRequest.getEmployeeId(),
                advanceRequest.getAmount().doubleValue(),
                advanceRequest.getRequestReason(),
                advanceRequest.getStatus()
        );
        
        eventPublisher.publish(KafkaTopics.ADVANCE_REQUEST_CREATED, event);
    }
    
    /**
     * Publishes an advance request status updated event to Kafka.
     *
     * @param advanceRequest the updated advance request
     */
    public void publishAdvanceRequestStatusUpdatedEvent(AdvanceRequest advanceRequest) {
        log.info("Publishing advance request status updated event for ID: {}", advanceRequest.getId());
        
        AdvanceRequestStatusUpdatedEvent event = new AdvanceRequestStatusUpdatedEvent(
                advanceRequest.getId(),
                advanceRequest.getEmployeeId(),
                advanceRequest.getStatus(),
                advanceRequest.getReviewComment(),
                advanceRequest.getReviewerId()
        );
        
        eventPublisher.publish(KafkaTopics.ADVANCE_REQUEST_STATUS_UPDATED, event);
    }
}