package com.inventory.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveMaintenanceResponse {
    private Long assetId;
    private String assetDesignation;
    private String serialNumber;

    private int ticketCount;
    private int ageInDays;

    // Calculated insights
    private double failureProbability; // 0.0 to 1.0
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private Integer estimatedDaysToFailure;

    private String recommendation;
}

