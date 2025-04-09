package com.payrolladvance.kafka.common.events.user;

import com.payrolladvance.kafka.common.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreatedEvent extends BaseEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String kycStatus;
    private String phoneNumber;
    
    public UserCreatedEvent(Long userId, String email, String firstName, String lastName, 
                            String role, String kycStatus, String phoneNumber) {
        super("USER_CREATED");
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.kycStatus = kycStatus;
        this.phoneNumber = phoneNumber;
    }
}