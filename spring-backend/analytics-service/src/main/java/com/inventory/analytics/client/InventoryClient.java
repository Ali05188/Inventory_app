package com.inventory.analytics.client;

import com.inventory.analytics.dto.AssetDTO;
import com.inventory.analytics.dto.MaintenanceTicketDTO;
import com.inventory.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @GetMapping("/api/assets/{id}")
    ApiResponse<AssetDTO> getAssetById(@PathVariable("id") Long id);

    @GetMapping("/api/maintenances/asset/{assetId}")
    ApiResponse<List<MaintenanceTicketDTO>> getTicketsByAsset(@PathVariable("assetId") Long assetId);
}

