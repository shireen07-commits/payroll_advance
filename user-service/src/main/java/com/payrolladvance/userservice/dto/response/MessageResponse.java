package com.payrolladvance.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO for message responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String message;
}