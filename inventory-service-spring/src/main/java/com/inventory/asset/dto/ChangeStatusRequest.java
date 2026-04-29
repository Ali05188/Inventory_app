package com.inventory.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotBlank(message = "New status is required")
    private String newStatus;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}

