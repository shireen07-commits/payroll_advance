package com.payrolladvance.advanceservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payrolladvance.kafka.common.util.EventPublisher;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application configuration.
 */
@Configuration
public class AppConfig {
    
    /**
     * Creates a load-balanced RestTemplate bean.
     *
     * @param builder the RestTemplateBuilder
     * @return the RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
    
    /**
     * Creates an EventPublisher bean.
     *
     * @param kafkaTemplate the KafkaTemplate
     * @param objectMapper  the ObjectMapper
     * @return the EventPublisher
     */
    @Bean
    public EventPublisher eventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new EventPublisher(kafkaTemplate, objectMapper);
    }
}