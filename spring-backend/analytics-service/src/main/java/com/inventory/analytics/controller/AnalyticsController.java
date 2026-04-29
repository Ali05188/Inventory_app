package com.inventory.analytics.controller;

import com.inventory.analytics.dto.PredictiveMaintenanceResponse;
import com.inventory.analytics.service.PredictiveMaintenanceService;
import com.inventory.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final PredictiveMaintenanceService predictiveService;

    @GetMapping("/predictive-maintenance/asset/{assetId}")
    public ResponseEntity<ApiResponse<PredictiveMaintenanceResponse>> analyzeAsset(@PathVariable Long assetId) {
        PredictiveMaintenanceResponse response = predictiveService.analyzeAsset(assetId);
        return ResponseEntity.ok(ApiResponse.success("Predictive analysis completed", response));
    }
}

