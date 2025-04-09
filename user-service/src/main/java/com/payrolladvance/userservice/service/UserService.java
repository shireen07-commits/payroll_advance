package com.payrolladvance.userservice.service;

import com.payrolladvance.userservice.dto.UserRegistrationDto;
import com.payrolladvance.userservice.model.User;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {
    /**
     * Creates a new user based on registration data.
     *
     * @param registrationDto the user registration data
     * @return the created user
     */
    User createUser(UserRegistrationDto registrationDto);
    
    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return the user if found
     */
    User getUserById(Long id);
    
    /**
     * Gets a user by email.
     *
     * @param email the user email
     * @return the user if found
     */
    User getUserByEmail(String email);
    
    /**
     * Updates a user's KYC status.
     *
     * @param id the user ID
     * @param kycStatus the new KYC status
     * @return the updated user
     */
    User updateKycStatus(Long id, String kycStatus);
}