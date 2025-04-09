package com.payrolladvance.kafka.common.config;

/**
 * Central place to define all Kafka topics used in the application.
 * This ensures consistency across all services.
 */
public class KafkaTopics {
    // User service topics
    public static final String USER_CREATED = "user-created";
    public static final String USER_UPDATED = "user-updated";
    public static final String USER_KYC_VERIFIED = "user-kyc-verified";
    
    // Employee profile topics
    public static final String EMPLOYEE_PROFILE_CREATED = "employee-profile-created";
    public static final String EMPLOYEE_PROFILE_UPDATED = "employee-profile-updated";
    
    // Employer profile topics
    public static final String EMPLOYER_PROFILE_CREATED = "employer-profile-created";
    public static final String EMPLOYER_PROFILE_UPDATED = "employer-profile-updated";
    
    // Advance request topics
    public static final String ADVANCE_REQUEST_CREATED = "advance-request-created";
    public static final String ADVANCE_REQUEST_UPDATED = "advance-request-updated";
    public static final String ADVANCE_REQUEST_APPROVED = "advance-request-approved";
    public static final String ADVANCE_REQUEST_REJECTED = "advance-request-rejected";
    
    // Disbursement topics
    public static final String DISBURSEMENT_INITIATED = "disbursement-initiated";
    public static final String DISBURSEMENT_COMPLETED = "disbursement-completed";
    public static final String DISBURSEMENT_FAILED = "disbursement-failed";
    
    // Repayment topics
    public static final String REPAYMENT_SCHEDULED = "repayment-scheduled";
    public static final String REPAYMENT_COMPLETED = "repayment-completed";
    public static final String REPAYMENT_FAILED = "repayment-failed";
    
    // Notification topics
    public static final String NOTIFICATION_REQUESTED = "notification-requested";
    public static final String NOTIFICATION_DELIVERED = "notification-delivered";
}