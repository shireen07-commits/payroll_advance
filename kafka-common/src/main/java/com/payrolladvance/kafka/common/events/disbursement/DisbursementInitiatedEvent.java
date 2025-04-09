package com.payrolladvance.kafka.common.events.disbursement;

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
public class DisbursementInitiatedEvent extends BaseEvent {
    private Long disbursementId;
    private Long advanceRequestId;
    private Long employeeId;
    private Long userId;  // User ID of the employee
    private BigDecimal amount;
    private BigDecimal feeAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionReference;
    private LocalDateTime disbursementDate;
    
    public DisbursementInitiatedEvent(Long disbursementId, Long advanceRequestId, Long employeeId, 
                                    Long userId, BigDecimal amount, BigDecimal feeAmount, 
                                    BigDecimal totalAmount, String paymentStatus, 
                                    String paymentMethod, String transactionReference,
                                    LocalDateTime disbursementDate) {
        super("DISBURSEMENT_INITIATED");
        this.disbursementId = disbursementId;
        this.advanceRequestId = advanceRequestId;
        this.employeeId = employeeId;
        this.userId = userId;
        this.amount = amount;
        this.feeAmount = feeAmount;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.disbursementDate = disbursementDate;
    }
}