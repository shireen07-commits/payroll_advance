package com.payrolladvance.advanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an advance request status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceRequestUpdateDto {
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private Long approvedBy;
    
    private String rejectionReason;
}