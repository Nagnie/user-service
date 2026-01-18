package com.example.userservice.config;

import com.example.userservice.entity.User;
import com.example.userservice.enums.AccountType;
import com.example.userservice.enums.Role;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .role(Role.ROLE_ADMIN)
                    .accountType(AccountType.REGULAR)
                    .isActive(true)
                    .isEmailVerified(true)
                    .build();
            
            userRepository.save(admin);
            System.out.println("=================================================");
            System.out.println("Admin user created successfully!");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("=================================================");
        }
        
        // Create sample regular user if not exists
        if (!userRepository.existsByUsername("user")) {
            User regularUser = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("Regular User")
                    .role(Role.ROLE_USER)
                    .accountType(AccountType.REGULAR)
                    .isActive(true)
                    .isEmailVerified(true)
                    .build();
            
            userRepository.save(regularUser);
            System.out.println("=================================================");
            System.out.println("Sample user created successfully!");
            System.out.println("Username: user");
            System.out.println("Password: user123");
            System.out.println("=================================================");
        }
    }
}