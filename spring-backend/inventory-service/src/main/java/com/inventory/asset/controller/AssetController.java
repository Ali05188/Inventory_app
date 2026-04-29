package com.inventory.asset.controller;

import com.inventory.asset.dto.AssetDTO;
import com.inventory.asset.dto.AssetRequest;
import com.inventory.asset.service.AssetService;
import com.inventory.common.dto.ApiResponse;
import com.inventory.common.enums.AssetStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AssetDTO>>> getAllAssets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) AssetStatus status,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        Page<AssetDTO> assets = assetService.findAll(search, projectId, supplierId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(assets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetDTO>> getAssetById(@PathVariable Long id) {
        AssetDTO asset = assetService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(asset));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AssetDTO>> createAsset(@Valid @RequestBody AssetRequest request) {
        AssetDTO asset = assetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asset created successfully", asset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetDTO>> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request
    ) {
        AssetDTO asset = assetService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Asset updated successfully", asset));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Asset deleted successfully", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AssetDTO>> changeStatus(
            @PathVariable Long id,
            @RequestParam AssetStatus status,
            @RequestParam(required = false) String reason
    ) {
        AssetDTO asset = assetService.changeStatus(id, status, reason);
        return ResponseEntity.ok(ApiResponse.success("Status changed successfully", asset));
    }
}

