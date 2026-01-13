package com.example.userservice.service;

import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.enums.AccountType;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToUserResponse(user);
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update email if provided and not already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(request.getEmail());
            user.setIsEmailVerified(false);
        }
        
        // Update full name
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        
        // Update phone number
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        // Update password if provided
        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null) {
                throw new BadRequestException("Current password is required to change password");
            }
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }
            
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public void deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Soft delete - deactivate account
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    @Transactional
    public UserResponse upgradeToVip(Long userId, int months) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setAccountType(AccountType.VIP);
        
        LocalDateTime vipExpiry = user.getVipExpiryDate();
        if (vipExpiry == null || vipExpiry.isBefore(LocalDateTime.now())) {
            vipExpiry = LocalDateTime.now();
        }
        user.setVipExpiryDate(vipExpiry.plusMonths(months));
        
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse downgradeToRegular(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setAccountType(AccountType.REGULAR);
        user.setVipExpiryDate(null);
        
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getVipUsers() {
        return userRepository.findByAccountType(AccountType.VIP).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .accountType(user.getAccountType())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .vipExpiryDate(user.getVipExpiryDate())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}