package com.inventory.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTicketDTO {
    private Long id;
    private Long assetId;
    private String priority;
    private String status;
}

