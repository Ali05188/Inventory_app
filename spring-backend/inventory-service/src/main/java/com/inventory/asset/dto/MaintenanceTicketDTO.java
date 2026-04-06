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
public class MaintenanceTicketDTO {
    private Long id;
    private Long assetId;
    private String issueDescription;
    private String resolutionNotes;
    private String priority;
    private String status;
    private LocalDate reportedDate;
    private LocalDate resolvedDate;
    private LocalDateTime createdAt;
}

