package com.inventory.asset.controller;

import com.inventory.asset.repository.AssetRepository;
import com.inventory.asset.repository.ProjectRepository;
import com.inventory.asset.repository.SupplierRepository;
import com.inventory.asset.repository.LocationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics APIs")
public class DashboardController {

    private final AssetRepository assetRepository;
    private final ProjectRepository projectRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view dashboard') or hasAuthority('view assets')")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total counts
        stats.put("totalAssets", assetRepository.count());
        stats.put("totalProjects", projectRepository.count());
        stats.put("totalSuppliers", supplierRepository.count());
        stats.put("totalLocations", locationRepository.count());

        // Asset counts by status
        Map<String, Long> assetsByStatus = new HashMap<>();
        assetsByStatus.put("new", assetRepository.countByStatus("new"));
        assetsByStatus.put("in_service", assetRepository.countByStatus("in_service"));
        assetsByStatus.put("maintenance", assetRepository.countByStatus("maintenance"));
        assetsByStatus.put("repair", assetRepository.countByStatus("repair"));
        assetsByStatus.put("decommissioned", assetRepository.countByStatus("decommissioned"));
        assetsByStatus.put("disposed", assetRepository.countByStatus("disposed"));
        stats.put("assetsByStatus", assetsByStatus);

        return ResponseEntity.ok(stats);
    }
}

