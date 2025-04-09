package com.payrolladvance.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an employer profile.
 * Contains company information and settings.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employer_profiles")
public class EmployerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;
    
    @Column(name = "tax_id")
    private String taxId;
    
    @Column(name = "industry")
    private String industry;
    
    @Column(name = "number_of_employees")
    private Integer numberOfEmployees;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "contact_person_name")
    private String contactPersonName;
    
    @Column(name = "contact_person_email")
    private String contactPersonEmail;
    
    @Column(name = "contact_person_phone")
    private String contactPersonPhone;
    
    @Column(name = "street_address")
    private String streetAddress;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "max_advance_percent", columnDefinition = "DOUBLE DEFAULT 50.0")
    private Double maxAdvancePercent;
    
    @Column(name = "approval_workflow_required", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean approvalWorkflowRequired;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (maxAdvancePercent == null) {
            maxAdvancePercent = 50.0;
        }
        if (approvalWorkflowRequired == null) {
            approvalWorkflowRequired = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}