package com.payrolladvance.userservice.model;

/**
 * Enumeration of user roles in the system.
 */
public enum UserRole {
    ROLE_EMPLOYEE,  // Regular employee who can request salary advances
    ROLE_EMPLOYER,  // Employer/HR who can review and approve advances
    ROLE_ADMIN      // System administrator with full access
}