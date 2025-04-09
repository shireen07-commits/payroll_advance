package com.payrolladvance.kafka.common.events;

/**
 * Enum for different types of events in the system.
 */
public enum EventType {
    // User events
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    USER_VERIFIED,
    
    // Employee profile events
    EMPLOYEE_PROFILE_CREATED,
    EMPLOYEE_PROFILE_UPDATED,
    EMPLOYEE_PROFILE_DELETED,
    
    // Employer profile events
    EMPLOYER_PROFILE_CREATED,
    EMPLOYER_PROFILE_UPDATED,
    EMPLOYER_PROFILE_DELETED,
    
    // Advance request events
    ADVANCE_REQUEST_CREATED,
    ADVANCE_REQUEST_UPDATED,
    ADVANCE_REQUEST_APPROVED,
    ADVANCE_REQUEST_REJECTED,
    
    // Disbursement events
    DISBURSEMENT_CREATED,
    DISBURSEMENT_UPDATED,
    DISBURSEMENT_COMPLETED,
    DISBURSEMENT_FAILED,
    
    // Repayment events
    REPAYMENT_CREATED,
    REPAYMENT_UPDATED,
    REPAYMENT_COMPLETED,
    REPAYMENT_FAILED,
    
    // Notification events
    NOTIFICATION_CREATED,
    NOTIFICATION_READ
}