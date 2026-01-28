package com.example.userservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVIPUpgradeRequestDTO {

    @NotNull(message = "Requested months is required")
    @Min(value = 1, message = "Minimum 1 month required")
    private Integer requestedMonths;

    private String message;
}
