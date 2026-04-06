package com.inventory.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaintenanceTicketRequest {
    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    private String priority; // optional, defaults to MEDIUM
}

