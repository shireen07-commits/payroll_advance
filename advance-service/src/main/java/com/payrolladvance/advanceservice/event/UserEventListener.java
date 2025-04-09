package com.payrolladvance.advanceservice.event;

import com.payrolladvance.kafka.common.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Listener for user events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    
    /**
     * Listens for user events to handle anything related to users in the advance service.
     *
     * @param event the user event
     */
    @KafkaListener(topics = "user-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserEvent(Map<String, Object> event) {
        log.info("Received user event: {}", event);
        
        // Extract event type
        String eventTypeStr = (String) event.get("eventType");
        EventType eventType = EventType.valueOf(eventTypeStr);
        
        // Handle user verification event (if user is verified, they can request advances)
        if (eventType == EventType.USER_VERIFIED) {
            Long userId = Long.valueOf((Integer) event.get("entityId"));
            log.info("User with ID {} has been verified, now eligible for advances", userId);
            
            // In a real implementation, we might update a local cache or database
            // to track which users are eligible for advances
        }
    }
}