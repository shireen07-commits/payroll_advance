package com.payrolladvance.user.entity;

public enum ApprovalWorkflowType {
    MANUAL, // Requires manual approval for all advance requests
    AUTOMATED, // Automated approval based on rules
    HYBRID // Hybrid approach (automated up to a threshold, manual above that)
}