package com.inventory.asset.controller;

import com.inventory.asset.entity.AssetType;
import com.inventory.asset.repository.AssetTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-types")
@RequiredArgsConstructor
@Tag(name = "Asset Type Management", description = "Asset type management APIs")
public class AssetTypeController {

    private final AssetTypeRepository assetTypeRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view assets')")
    @Operation(summary = "Get all asset types", description = "Retrieve list of all asset types")
    public ResponseEntity<List<AssetType>> getAllAssetTypes() {
        return ResponseEntity.ok(assetTypeRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view assets')")
    @Operation(summary = "Get asset type by ID", description = "Retrieve a specific asset type")
    public ResponseEntity<AssetType> getAssetTypeById(@PathVariable Long id) {
        AssetType assetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset type not found with id: " + id));
        return ResponseEntity.ok(assetType);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create assets')")
    @Operation(summary = "Create asset type", description = "Create a new asset type")
    public ResponseEntity<AssetType> createAssetType(@RequestBody AssetType assetType) {
        if (assetTypeRepository.existsByName(assetType.getName())) {
            throw new RuntimeException("Asset type name already exists");
        }
        return ResponseEntity.ok(assetTypeRepository.save(assetType));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit assets')")
    @Operation(summary = "Update asset type", description = "Update an existing asset type")
    public ResponseEntity<AssetType> updateAssetType(@PathVariable Long id, @RequestBody AssetType assetTypeDetails) {
        AssetType assetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset type not found with id: " + id));

        assetType.setName(assetTypeDetails.getName());
        assetType.setDescription(assetTypeDetails.getDescription());

        return ResponseEntity.ok(assetTypeRepository.save(assetType));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete assets')")
    @Operation(summary = "Delete asset type", description = "Delete an asset type")
    public ResponseEntity<String> deleteAssetType(@PathVariable Long id) {
        AssetType assetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset type not found with id: " + id));
        assetTypeRepository.delete(assetType);
        return ResponseEntity.ok("Asset type deleted successfully");
    }
}

