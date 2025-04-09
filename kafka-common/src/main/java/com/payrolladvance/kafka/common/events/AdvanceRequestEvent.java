package com.payrolladvance.kafka.common.events;

import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event class for advance request events.
 */
@NoArgsConstructor
public class AdvanceRequestEvent extends BaseEvent<Map<String, Object>> {
    
    /**
     * Constructs a new AdvanceRequestEvent with the given entity ID, event type, and payload.
     *
     * @param entityId  the entity ID
     * @param eventType the event type
     * @param payload   the payload
     */
    public AdvanceRequestEvent(Long entityId, EventType eventType, Map<String, Object> payload) {
        super(entityId, eventType, payload);
    }
    
    /**
     * Constructs a new AdvanceRequestEvent with the given entity ID, event type, and object payload.
     *
     * @param entityId  the entity ID
     * @param eventType the event type
     * @param payload   the payload object
     */
    public AdvanceRequestEvent(Long entityId, EventType eventType, Object payload) {
        super(entityId, eventType, convertToMap(payload));
    }
    
    /**
     * Converts the given object to a map using reflection.
     *
     * @param object the object to convert
     * @return a map representation of the object
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> convertToMap(Object object) {
        // In a real implementation, this would use reflection or JSON conversion
        // For simplicity, we're assuming the object is already a Map
        return (Map<String, Object>) object;
    }
}