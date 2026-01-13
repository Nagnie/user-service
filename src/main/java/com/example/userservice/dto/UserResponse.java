package com.example.userservice.dto;

import com.example.userservice.enums.AccountType;
import com.example.userservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
    private AccountType accountType;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private LocalDateTime vipExpiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}