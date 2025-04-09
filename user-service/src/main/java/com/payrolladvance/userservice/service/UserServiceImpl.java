package com.payrolladvance.userservice.service;

import com.payrolladvance.userservice.dto.UserRegistrationDto;
import com.payrolladvance.userservice.event.UserEventListener;
import com.payrolladvance.userservice.exception.UserNotFoundException;
import com.payrolladvance.userservice.model.User;
import com.payrolladvance.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventListener userEventListener;
    
    @Override
    @Transactional
    public User createUser(UserRegistrationDto registrationDto) {
        log.info("Creating new user with email: {}", registrationDto.getEmail());
        
        // Check if user already exists
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + registrationDto.getEmail() + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setRole(registrationDto.getRole());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setKycStatus("NOT_STARTED");
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Publish user created event
        userEventListener.publishUserCreatedEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole(),
                savedUser.getKycStatus(),
                savedUser.getPhoneNumber()
        );
        
        return savedUser;
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    
    @Override
    @Transactional
    public User updateKycStatus(Long id, String kycStatus) {
        User user = getUserById(id);
        user.setKycStatus(kycStatus);
        return userRepository.save(user);
    }
}