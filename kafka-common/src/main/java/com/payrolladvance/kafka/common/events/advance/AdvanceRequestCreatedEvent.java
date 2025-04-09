package com.payrolladvance.kafka.common.events.advance;

import com.payrolladvance.kafka.common.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdvanceRequestCreatedEvent extends BaseEvent {
    private Long requestId;
    private Long employeeId;
    private Long userId;  // User ID of the employee
    private BigDecimal requestedAmount;
    private String reason;
    private String status;
    private LocalDateTime requestDate;
    
    public AdvanceRequestCreatedEvent(Long requestId, Long employeeId, Long userId, 
                                     BigDecimal requestedAmount, String reason, 
                                     String status, LocalDateTime requestDate) {
        super("ADVANCE_REQUEST_CREATED");
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.userId = userId;
        this.requestedAmount = requestedAmount;
        this.reason = reason;
        this.status = status;
        this.requestDate = requestDate;
    }
}