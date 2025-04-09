package com.payrolladvance.user.entity;

public enum KycStatus {
    PENDING,     // KYC verification is pending
    IN_PROGRESS, // KYC verification is in progress
    VERIFIED,    // KYC verification is complete and verified
    REJECTED     // KYC verification was rejected
}