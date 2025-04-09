package com.payrolladvance.kafka.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all Kafka events.
 *
 * @param <T> the type of the payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent<T> {
    
    /**
     * Unique ID for the event.
     */
    private String eventId;
    
    /**
     * ID of the entity associated with the event.
     */
    private Long entityId;
    
    /**
     * Type of the event.
     */
    private EventType eventType;
    
    /**
     * Timestamp when the event was created.
     */
    private LocalDateTime timestamp;
    
    /**
     * Payload of the event.
     */
    private T payload;
    
    /**
     * Constructs a new BaseEvent with the given entity ID, event type, and payload.
     *
     * @param entityId  the entity ID
     * @param eventType the event type
     * @param payload   the payload
     */
    public BaseEvent(Long entityId, EventType eventType, T payload) {
        this.eventId = UUID.randomUUID().toString();
        this.entityId = entityId;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.payload = payload;
    }
}