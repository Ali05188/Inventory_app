package com.inventory.analytics.service;

import com.inventory.analytics.client.InventoryClient;
import com.inventory.analytics.dto.AssetDTO;
import com.inventory.analytics.dto.MaintenanceTicketDTO;
import com.inventory.analytics.dto.PredictiveMaintenanceResponse;
import com.inventory.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictiveMaintenanceService {

    private final InventoryClient inventoryClient;

    public PredictiveMaintenanceResponse analyzeAsset(Long assetId) {

        ApiResponse<AssetDTO> assetResponse = inventoryClient.getAssetById(assetId);
        if (assetResponse == null || assetResponse.getData() == null) {
            throw new RuntimeException("Asset not found or error communicating with inventory service");
        }
        AssetDTO asset = assetResponse.getData();

        ApiResponse<List<MaintenanceTicketDTO>> ticketResponse = inventoryClient.getTicketsByAsset(assetId);
        List<MaintenanceTicketDTO> tickets = (ticketResponse != null && ticketResponse.getData() != null)
                ? ticketResponse.getData()
                : List.of();

        return performHeuristicAnalysis(asset, tickets);
    }

    private PredictiveMaintenanceResponse performHeuristicAnalysis(AssetDTO asset, List<MaintenanceTicketDTO> tickets) {
        int ageInDays = 0;
        if (asset.getServiceStartDate() != null) {
            ageInDays = (int) ChronoUnit.DAYS.between(asset.getServiceStartDate(), LocalDate.now());
        } else {
            // Default assumption if not yet in service
            ageInDays = 10;
        }

        int score = 0;

        // 1. Age penalty (assume expected life is roughly 5 years = 1825 days)
        double ageFactor = Math.min((double) ageInDays / 1825.0, 1.0);
        score += (int) (ageFactor * 40); // Up to 40 points for age

        // 2. Ticket history penalty
        int ticketCount = tickets.size();
        for (MaintenanceTicketDTO ticket : tickets) {
            if ("CRITICAL".equalsIgnoreCase(ticket.getPriority())) {
                score += 15;
            } else if ("HIGH".equalsIgnoreCase(ticket.getPriority())) {
                score += 10;
            } else {
                score += 5;
            }
        }

        // Cap at 100
        score = Math.min(score, 100);
        double failureProbability = score / 100.0;

        String riskLevel;
        String recommendation;
        Integer daysToFailure = null;

        if (score >= 80) {
            riskLevel = "CRITICAL";
            recommendation = "Immediate maintenance or replacement required. Very high risk of failure.";
            daysToFailure = Math.max(7, 30 - ticketCount * 5);
        } else if (score >= 50) {
            riskLevel = "HIGH";
            recommendation = "Schedule inspection soon. Asset is showing signs of degradation.";
            daysToFailure = Math.max(14, 90 - (score - 50));
        } else if (score >= 20) {
            riskLevel = "MEDIUM";
            recommendation = "Normal degradation. Monitor regularly.";
            daysToFailure = Math.max(90, 180 - (score - 20));
        } else {
            riskLevel = "LOW";
            recommendation = "Asset is in good condition.";
            daysToFailure = null; // Too long to estimate properly
        }

        return PredictiveMaintenanceResponse.builder()
                .assetId(asset.getId())
                .assetDesignation(asset.getDesignation())
                .serialNumber(asset.getSerialNumber())
                .ticketCount(ticketCount)
                .ageInDays(ageInDays)
                .failureProbability(failureProbability)
                .riskLevel(riskLevel)
                .estimatedDaysToFailure(daysToFailure)
                .recommendation(recommendation)
                .build();
    }
}

