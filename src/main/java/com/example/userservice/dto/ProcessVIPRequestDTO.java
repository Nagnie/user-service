package com.example.userservice.dto;

import com.example.userservice.entity.VIPUpgradeRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessVIPRequestDTO {

    @NotNull(message = "Status is required")
    private VIPUpgradeRequest.RequestStatus status;

    private String adminNote;
}
