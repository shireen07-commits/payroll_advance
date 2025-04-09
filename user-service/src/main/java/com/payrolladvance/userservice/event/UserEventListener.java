package com.payrolladvance.userservice.event;

import com.payrolladvance.kafka.common.config.KafkaTopics;
import com.payrolladvance.kafka.common.events.advance.AdvanceRequestCreatedEvent;
import com.payrolladvance.kafka.common.events.notification.NotificationRequestedEvent;
import com.payrolladvance.kafka.common.events.user.UserCreatedEvent;
import com.payrolladvance.kafka.common.util.EventPublisher;
import com.payrolladvance.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Component that listens to Kafka events related to users.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    
    private final UserService userService;
    private final EventPublisher eventPublisher;
    
    /**
     * Listens to advance request created events.
     * This is needed to update any user or profile data related to advance requests.
     *
     * @param event the advance request created event
     */
    @KafkaListener(topics = KafkaTopics.ADVANCE_REQUEST_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleAdvanceRequestCreated(AdvanceRequestCreatedEvent event) {
        log.info("Received advance request created event: {}", event);
        // Update user statistics or handle other logic related to advance requests
    }
    
    /**
     * Listens to notification requested events.
     * This allows the user service to be notified when any service creates a notification for a user.
     *
     * @param event the notification requested event
     */
    @KafkaListener(topics = KafkaTopics.NOTIFICATION_REQUESTED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleNotificationRequested(NotificationRequestedEvent event) {
        log.info("Received notification requested event: {}", event);
        // Handle notification logic if needed
    }
    
    /**
     * Publishes a user created event to Kafka.
     * This is called by the user service when a new user is created.
     *
     * @param userId user ID
     * @param email user email
     * @param firstName user first name
     * @param lastName user last name
     * @param role user role
     * @param kycStatus user KYC status
     * @param phoneNumber user phone number
     */
    public void publishUserCreatedEvent(Long userId, String email, String firstName, String lastName, 
                                      String role, String kycStatus, String phoneNumber) {
        log.info("Publishing user created event for user ID: {}", userId);
        
        UserCreatedEvent event = new UserCreatedEvent(
                userId, email, firstName, lastName, role, kycStatus, phoneNumber
        );
        
        eventPublisher.publish(KafkaTopics.USER_CREATED, event);
    }
}