package com.inventory.asset.controller;

import com.inventory.asset.dto.*;
import com.inventory.asset.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Management", description = "Asset management APIs")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @PreAuthorize("hasAuthority('view assets')")
    @Operation(summary = "Get all assets", description = "Retrieve paginated list of all assets with optional filters")
    public ResponseEntity<Page<AssetResponse>> getAllAssets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        AssetSearchCriteria criteria = AssetSearchCriteria.builder()
                .search(search)
                .projectId(projectId)
                .supplierId(supplierId)
                .status(status)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();

        Page<AssetResponse> assets = assetService.getAllAssets(criteria, pageable);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view assets')")
    @Operation(summary = "Get asset by ID", description = "Retrieve a specific asset by its ID")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable Long id) {
        AssetResponse asset = assetService.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create assets')")
    @Operation(summary = "Create asset", description = "Create a new asset")
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        AssetResponse asset = assetService.createAsset(request);
        return ResponseEntity.ok(asset);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit assets')")
    @Operation(summary = "Update asset", description = "Update an existing asset")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody CreateAssetRequest request) {
        AssetResponse asset = assetService.updateAsset(id, request);
        return ResponseEntity.ok(asset);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete assets')")
    @Operation(summary = "Delete asset", description = "Soft delete an asset by its ID")
    public ResponseEntity<String> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok("Asset deleted successfully");
    }

    @PostMapping("/{id}/change-status")
    @PreAuthorize("hasAuthority('change asset status')")
    @Operation(summary = "Change asset status", description = "Change the status of an asset")
    public ResponseEntity<AssetResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {
        AssetResponse asset = assetService.changeStatus(id, request);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/{id}/allowed-transitions")
    @PreAuthorize("hasAuthority('view assets')")
    @Operation(summary = "Get allowed transitions", description = "Get allowed status transitions for an asset")
    public ResponseEntity<List<String>> getAllowedTransitions(@PathVariable Long id) {
        List<String> transitions = assetService.getAllowedTransitions(id);
        return ResponseEntity.ok(transitions);
    }
}

