package com.payrolladvance.kafka.common.util;

import com.payrolladvance.kafka.common.events.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for publishing events to Kafka topics.
 */
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {
    
    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;
    
    /**
     * Publishes an event to the specified topic.
     *
     * @param topic the topic to publish to
     * @param event the event to publish
     */
    public void publish(String topic, BaseEvent<?> event) {
        log.info("Publishing event to topic {}: {}", topic, event);
        
        CompletableFuture<SendResult<String, BaseEvent<?>>> future = kafkaTemplate.send(topic, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published successfully to topic {} with offset {}", 
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event to topic {}: {}", topic, ex.getMessage(), ex);
            }
        });
    }
}