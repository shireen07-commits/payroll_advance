package com.payrolladvance.advanceservice.exception;

/**
 * Exception thrown when an advance request is not found.
 */
public class AdvanceRequestNotFoundException extends RuntimeException {
    public AdvanceRequestNotFoundException(String message) {
        super(message);
    }
}