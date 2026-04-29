package com.inventory.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetAssignmentDTO {
    private Long id;
    private Long assetId;
    private Long assignedToUserId;
    private LocalDate assignmentDate;
    private LocalDate returnDate;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
}

