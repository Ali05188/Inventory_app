package com.inventory.asset.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AssetAssignmentRequest {
    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Assigned user ID is required")
    private Long assignedToUserId;

    @NotNull(message = "Assignment date is required")
    private LocalDate assignmentDate;

    private String notes;
}

