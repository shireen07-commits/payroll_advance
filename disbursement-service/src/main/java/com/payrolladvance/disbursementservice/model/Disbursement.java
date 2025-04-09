package com.payrolladvance.disbursementservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a disbursement in the system.
 */
@Data
@Entity
@Table(name = "disbursements")
@NoArgsConstructor
@AllArgsConstructor
public class Disbursement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "advance_request_id", nullable = false)
    private Long advanceRequestId;
    
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "transaction_reference")
    private String transactionReference;
    
    @Column(name = "status", nullable = false)
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    @Column(name = "expected_repayment_date")
    private LocalDateTime expectedRepaymentDate;
    
    @Column(name = "fee_amount")
    private BigDecimal feeAmount;
    
    @Column(name = "total_repayment_amount")
    private BigDecimal totalRepaymentAmount;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}