package com.payrolladvance.kafka.common.events.advance;

import com.payrolladvance.kafka.common.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdvanceRequestStatusChangedEvent extends BaseEvent {
    private Long requestId;
    private Long employeeId;
    private Long userId;  // User ID of the employee
    private String oldStatus;
    private String newStatus;
    private Long approvedBy;  // User ID of the approver (if applicable)
    private LocalDateTime statusChangeDate;
    
    public AdvanceRequestStatusChangedEvent(Long requestId, Long employeeId, Long userId,
                                           String oldStatus, String newStatus, 
                                           Long approvedBy, LocalDateTime statusChangeDate) {
        super("ADVANCE_REQUEST_STATUS_CHANGED");
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.approvedBy = approvedBy;
        this.statusChangeDate = statusChangeDate;
    }
}