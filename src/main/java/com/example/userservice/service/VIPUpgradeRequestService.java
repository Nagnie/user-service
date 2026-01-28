package com.example.userservice.service;

import com.example.userservice.dto.CreateVIPUpgradeRequestDTO;
import com.example.userservice.dto.ProcessVIPRequestDTO;
import com.example.userservice.dto.VIPUpgradeRequestDTO;
import com.example.userservice.entity.User;
import com.example.userservice.entity.VIPUpgradeRequest;
import com.example.userservice.enums.AccountType;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.VIPUpgradeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VIPUpgradeRequestService {

    private final VIPUpgradeRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public VIPUpgradeRequestDTO createRequest(Long userId, CreateVIPUpgradeRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAccountType() == AccountType.VIP) {
            throw new RuntimeException("User is already VIP");
        }

        // Check if user already has a pending request
        requestRepository.findByUserAndStatus(user, VIPUpgradeRequest.RequestStatus.PENDING)
                .ifPresent(req -> {
                    throw new RuntimeException("You already have a pending upgrade request");
                });

        VIPUpgradeRequest request = new VIPUpgradeRequest();
        request.setUser(user);
        request.setRequestedMonths(dto.getRequestedMonths());
        request.setMessage(dto.getMessage());
        request.setStatus(VIPUpgradeRequest.RequestStatus.PENDING);
        request.setRequestDate(LocalDateTime.now());

        VIPUpgradeRequest saved = requestRepository.save(request);
        return convertToDTO(saved);
    }

    public List<VIPUpgradeRequestDTO> getAllRequests() {
        return requestRepository.findAllByOrderByRequestDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VIPUpgradeRequestDTO> getPendingRequests() {
        return requestRepository.findByStatus(VIPUpgradeRequest.RequestStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VIPUpgradeRequestDTO> getUserRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return requestRepository.findByUserOrderByRequestDateDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VIPUpgradeRequestDTO processRequest(Long requestId, Long adminId, ProcessVIPRequestDTO dto) {
        VIPUpgradeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != VIPUpgradeRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        request.setStatus(dto.getStatus());
        request.setProcessedDate(LocalDateTime.now());
        request.setProcessedBy(adminId);
        request.setAdminNote(dto.getAdminNote());

        // If approved, upgrade user to VIP
        if (dto.getStatus() == VIPUpgradeRequest.RequestStatus.APPROVED) {
            userService.upgradeToVip(request.getUser().getId(), request.getRequestedMonths());
        }

        VIPUpgradeRequest saved = requestRepository.save(request);
        return convertToDTO(saved);
    }

    @Transactional
    public void cancelRequest(Long requestId, Long userId) {
        VIPUpgradeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own requests");
        }

        if (request.getStatus() != VIPUpgradeRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        requestRepository.delete(request);
    }

    private VIPUpgradeRequestDTO convertToDTO(VIPUpgradeRequest request) {
        VIPUpgradeRequestDTO dto = new VIPUpgradeRequestDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUser().getId());
        dto.setUsername(request.getUser().getUsername());
        dto.setFullName(request.getUser().getFullName());
        dto.setRequestedMonths(request.getRequestedMonths());
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setRequestDate(request.getRequestDate());
        dto.setProcessedDate(request.getProcessedDate());
        dto.setAdminNote(request.getAdminNote());
        return dto;
    }
}
