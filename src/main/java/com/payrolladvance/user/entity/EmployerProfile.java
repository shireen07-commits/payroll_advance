package com.payrolladvance.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employer_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_registration_number", nullable = false, unique = true)
    private String companyRegistrationNumber;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "company_website")
    private String companyWebsite;

    @Column(name = "industry")
    private String industry;

    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Column(name = "contact_person_email")
    private String contactPersonEmail;

    @Column(name = "contact_person_phone")
    private String contactPersonPhone;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "approval_workflow_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalWorkflowType approvalWorkflowType;

    @Column(name = "automated_approval_threshold")
    private Double automatedApprovalThreshold;

    @Column(name = "default_advance_percentage")
    private Integer defaultAdvancePercentage;

    @Column(name = "payday")
    private Integer payday;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeProfile> employees = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to add employee
    public void addEmployee(EmployeeProfile employee) {
        employees.add(employee);
        employee.setEmployer(this);
    }

    // Helper method to remove employee
    public void removeEmployee(EmployeeProfile employee) {
        employees.remove(employee);
        employee.setEmployer(null);
    }
}