package com.payrolladvance.userservice.model;

/**
 * Enumeration of Know Your Customer (KYC) verification statuses.
 */
public enum KycStatus {
    NOT_STARTED,  // KYC verification process has not been started
    IN_PROGRESS,  // KYC verification process is in progress
    VERIFIED,     // KYC verification has been completed successfully
    REJECTED      // KYC verification has been rejected
}