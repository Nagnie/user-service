package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateCurrentUser(request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully", null));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/vip")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getVipUsers() {
        List<UserResponse> users = userService.getVipUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @PostMapping("/{id}/upgrade-vip")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> upgradeToVip(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int months) {
        UserResponse user = userService.upgradeToVip(id, months);
        return ResponseEntity.ok(ApiResponse.success("User upgraded to VIP", user));
    }
    
    @PostMapping("/{id}/downgrade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> downgradeToRegular(@PathVariable Long id) {
        UserResponse user = userService.downgradeToRegular(id);
        return ResponseEntity.ok(ApiResponse.success("User downgraded to regular", user));
    }
}