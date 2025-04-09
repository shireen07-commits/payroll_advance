package com.payrolladvance.advanceservice.service;

import com.payrolladvance.advanceservice.model.AdvanceRequest;
import com.payrolladvance.advanceservice.repository.AdvanceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementation of the EligibilityService interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EligibilityServiceImpl implements EligibilityService {
    
    private final AdvanceRequestRepository advanceRequestRepository;
    private final RestTemplate restTemplate;
    
    @Value("${user.service.url}")
    private String userServiceUrl;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEligibleForAdvance(Long employeeId, BigDecimal requestedAmount) {
        log.info("Checking eligibility for employee ID: {} requesting amount: {}", employeeId, requestedAmount);
        
        // 1. Check if employee has any outstanding advances
        List<AdvanceRequest> pendingAdvances = advanceRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .filter(request -> "APPROVED".equals(request.getStatus()) || "PENDING".equals(request.getStatus()))
                .toList();
        
        if (!pendingAdvances.isEmpty()) {
            log.info("Employee ID: {} has outstanding advances", employeeId);
            return false;
        }
        
        // 2. Check maximum eligible amount
        BigDecimal maxEligibleAmount = getMaxEligibleAmount(employeeId);
        boolean isEligible = requestedAmount.compareTo(maxEligibleAmount) <= 0;
        
        log.info("Employee ID: {} eligibility result: {}, max eligible amount: {}", 
                employeeId, isEligible, maxEligibleAmount);
        
        return isEligible;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getMaxEligibleAmount(Long employeeId) {
        log.info("Calculating max eligible amount for employee ID: {}", employeeId);
        
        // In a real implementation, we would:
        // 1. Call the salary service to get the employee's earned but unpaid salary
        // 2. Apply business rules (e.g., allow up to 50% of earned unpaid salary)
        
        try {
            // Example: Get employee salary information from user service
            @SuppressWarnings("unchecked")
            var response = restTemplate.getForObject(
                    userServiceUrl + "/api/employees/{id}/salary-info",
                    java.util.Map.class,
                    employeeId
            );
            
            if (response != null) {
                BigDecimal monthlySalary = new BigDecimal(response.get("monthlySalary").toString());
                BigDecimal earnedAmount = new BigDecimal(response.get("earnedAmount").toString());
                
                // Allow up to 50% of earned amount
                return earnedAmount.multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.DOWN);
            }
        } catch (Exception e) {
            log.error("Error getting salary info for employee ID: {}", employeeId, e);
            // Fallback to a default calculation if the service call fails
        }
        
        // Fallback calculation (would be replaced with actual logic in production)
        // This is just a placeholder implementation
        BigDecimal defaultMaxAmount = new BigDecimal("1000.00");
        log.info("Returning default max eligible amount: {} for employee ID: {}", defaultMaxAmount, employeeId);
        
        return defaultMaxAmount;
    }
}