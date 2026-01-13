package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String fullName;
    
    private String phoneNumber;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String currentPassword;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}