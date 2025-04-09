package com.payrolladvance.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for JWT authentication response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    
    // Additional fields to help the front-end
    private Long employeeProfileId;
    private Long employerProfileId;
}