package com.payrolladvance.kafka.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard Kafka configuration to be used by all services.
 * This ensures consistent configuration across all services.
 */
@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:payroll-advance-app}")
    private String groupId;

    @Value("${spring.kafka.producer.client-id:payroll-advance-producer}")
    private String clientId;

    /**
     * Producer configuration for sending messages to Kafka.
     * @return Producer factory configuration
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        
        // Additional recommended settings
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");  // Strongest durability guarantee
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // Avoid duplicates
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);  // Retry on temporary failures
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka template for sending messages.
     * @return Kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Consumer configuration for receiving messages from Kafka.
     * @return Consumer factory configuration
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Configure JsonDeserializer to trust all packages
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.payrolladvance.*");
        
        // Additional recommended settings
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Start from earliest if no offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // Manual commit for better control
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka listener container factory.
     * @return Kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Enable batch messaging if needed
        // factory.setBatchListener(true);
        return factory;
    }

    /**
     * Kafka admin client configuration.
     * @return Kafka admin client configuration
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Creates Kafka topics if they don't exist.
     * Add all required topics here.
     */
    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
            // User-related topics
            createTopic(KafkaTopics.USER_CREATED),
            createTopic(KafkaTopics.USER_UPDATED),
            createTopic(KafkaTopics.USER_KYC_VERIFIED),
            
            // Employee profile topics
            createTopic(KafkaTopics.EMPLOYEE_PROFILE_CREATED),
            createTopic(KafkaTopics.EMPLOYEE_PROFILE_UPDATED),
            
            // Employer profile topics
            createTopic(KafkaTopics.EMPLOYER_PROFILE_CREATED),
            createTopic(KafkaTopics.EMPLOYER_PROFILE_UPDATED),
            
            // Advance request topics
            createTopic(KafkaTopics.ADVANCE_REQUEST_CREATED),
            createTopic(KafkaTopics.ADVANCE_REQUEST_UPDATED),
            createTopic(KafkaTopics.ADVANCE_REQUEST_APPROVED),
            createTopic(KafkaTopics.ADVANCE_REQUEST_REJECTED),
            
            // Disbursement topics
            createTopic(KafkaTopics.DISBURSEMENT_INITIATED),
            createTopic(KafkaTopics.DISBURSEMENT_COMPLETED),
            createTopic(KafkaTopics.DISBURSEMENT_FAILED),
            
            // Repayment topics
            createTopic(KafkaTopics.REPAYMENT_SCHEDULED),
            createTopic(KafkaTopics.REPAYMENT_COMPLETED),
            createTopic(KafkaTopics.REPAYMENT_FAILED),
            
            // Notification topics
            createTopic(KafkaTopics.NOTIFICATION_REQUESTED),
            createTopic(KafkaTopics.NOTIFICATION_DELIVERED)
        );
    }
    
    /**
     * Helper method to create a topic with standard configuration.
     * @param name The topic name
     * @return NewTopic instance
     */
    private NewTopic createTopic(String name) {
        return TopicBuilder
                .name(name)
                .partitions(3)  // Default to 3 partitions for parallelism
                .replicas(1)    // Default to 1 replica (can be increased in production)
                .build();
    }
}