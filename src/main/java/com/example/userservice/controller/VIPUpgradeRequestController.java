package com.example.userservice.controller;

import com.example.userservice.dto.CreateVIPUpgradeRequestDTO;
import com.example.userservice.dto.ProcessVIPRequestDTO;
import com.example.userservice.dto.VIPUpgradeRequestDTO;
import com.example.userservice.service.UserService;
import com.example.userservice.service.VIPUpgradeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vip-requests")
@RequiredArgsConstructor
public class VIPUpgradeRequestController {

    private final VIPUpgradeRequestService requestService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VIPUpgradeRequestDTO> createRequest(
            @Valid @RequestBody CreateVIPUpgradeRequestDTO dto) {
        Long userId = userService.getCurrentUser().getId();
        VIPUpgradeRequestDTO created = requestService.createRequest(userId, dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<VIPUpgradeRequestDTO>> getMyRequests() {
        Long userId = userService.getCurrentUser().getId();
        List<VIPUpgradeRequestDTO> requests = requestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long requestId) {
        Long userId = userService.getCurrentUser().getId();
        requestService.cancelRequest(requestId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VIPUpgradeRequestDTO>> getAllRequests() {
        List<VIPUpgradeRequestDTO> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VIPUpgradeRequestDTO>> getPendingRequests() {
        List<VIPUpgradeRequestDTO> requests = requestService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/admin/{requestId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VIPUpgradeRequestDTO> processRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody ProcessVIPRequestDTO dto) {
        Long adminId = userService.getCurrentUser().getId();
        VIPUpgradeRequestDTO processed = requestService.processRequest(requestId, adminId, dto);
        return ResponseEntity.ok(processed);
    }
}