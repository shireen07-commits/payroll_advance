package com.payrolladvance.advanceservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating an advance request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceRequestDto {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private LocalDateTime expectedRepaymentDate;
}