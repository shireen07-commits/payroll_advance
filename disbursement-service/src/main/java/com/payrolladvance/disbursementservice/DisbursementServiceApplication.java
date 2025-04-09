package com.payrolladvance.disbursementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the Disbursement Service.
 * This service handles advance payments disbursement and repayment tracking.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DisbursementServiceApplication {
    
    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DisbursementServiceApplication.class, args);
    }
}