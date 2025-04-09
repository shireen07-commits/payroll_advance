package com.payrolladvance.userservice.controller;

import com.payrolladvance.userservice.dto.UserRegistrationDto;
import com.payrolladvance.userservice.model.User;
import com.payrolladvance.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user-related endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Registers a new user.
     *
     * @param registrationDto the user registration data
     * @return the created user
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("Received registration request for email: {}", registrationDto.getEmail());
        User createdUser = userService.createUser(registrationDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return the user if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Updates a user's KYC status.
     *
     * @param id the user ID
     * @param kycStatus the new KYC status
     * @return the updated user
     */
    @PatchMapping("/{id}/kyc-status")
    public ResponseEntity<User> updateKycStatus(@PathVariable Long id, @RequestParam String kycStatus) {
        log.info("Updating KYC status for user ID {} to {}", id, kycStatus);
        User updatedUser = userService.updateKycStatus(id, kycStatus);
        return ResponseEntity.ok(updatedUser);
    }
}