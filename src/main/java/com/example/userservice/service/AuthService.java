package com.example.userservice.service;

import com.example.userservice.dto.*;
import com.example.userservice.entity.User;
import com.example.userservice.enums.AccountType;
import com.example.userservice.enums.Role;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_USER)
                .accountType(AccountType.REGULAR)
                .isActive(true)
                .isEmailVerified(false)
                .build();
        
        user = userRepository.save(user);
        
        return mapToUserResponse(user);
    }
    
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate token
        String token = tokenProvider.generateToken(authentication);
        
        // Update last login
        User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
        ).orElseThrow();
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        return AuthResponse.builder()
                .token(token)
                .expiresIn(tokenProvider.getExpirationTime())
                .user(mapToUserResponse(user))
                .build();
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