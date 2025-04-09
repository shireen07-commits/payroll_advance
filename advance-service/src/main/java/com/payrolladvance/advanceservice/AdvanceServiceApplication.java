package com.payrolladvance.advanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the Advance Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AdvanceServiceApplication {
    
    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AdvanceServiceApplication.class, args);
    }
}