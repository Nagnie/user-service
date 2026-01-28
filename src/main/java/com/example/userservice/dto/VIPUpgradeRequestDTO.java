package com.example.userservice.dto;

import com.example.userservice.entity.VIPUpgradeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VIPUpgradeRequestDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private Integer requestedMonths;
    private String message;
    private VIPUpgradeRequest.RequestStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private String adminNote;
}
