package com.inventory.asset.service;

import com.inventory.asset.dto.AssetDTO;
import com.inventory.asset.dto.AssetRequest;
import com.inventory.asset.entity.*;
import com.inventory.asset.repository.*;
import com.inventory.common.enums.AssetStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final SupplierRepository supplierRepository;
    private final ProjectRepository projectRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final LocationRepository locationRepository;

    public Page<AssetDTO> findAll(String search, Long projectId, Long supplierId, AssetStatus status, Pageable pageable) {
        return assetRepository.findWithFilters(search, projectId, supplierId, status, pageable)
                .map(this::toDTO);
    }

    public AssetDTO findById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));
        return toDTO(asset);
    }

    @Transactional
    public AssetDTO create(AssetRequest request) {
        if (request.getSerialNumber() != null && assetRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new RuntimeException("Serial number already exists");
        }

        Asset asset = Asset.builder()
                .cabNumber(request.getCabNumber())
                .projectCode(request.getProjectCode())
                .assetNumber(request.getAssetNumber())
                .designation(request.getDesignation())
                .serialNumber(request.getSerialNumber())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .deliveryDate(request.getDeliveryDate())
                .status(AssetStatus.PENDING)
                .build();

        setRelations(asset, request);
        asset = assetRepository.save(asset);
        return toDTO(asset);
    }

    @Transactional
    public AssetDTO update(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));

        asset.setCabNumber(request.getCabNumber());
        asset.setProjectCode(request.getProjectCode());
        asset.setAssetNumber(request.getAssetNumber());
        asset.setDesignation(request.getDesignation());
        asset.setSerialNumber(request.getSerialNumber());
        asset.setQuantity(request.getQuantity());
        asset.setUnitPrice(request.getUnitPrice());
        asset.setDeliveryDate(request.getDeliveryDate());

        setRelations(asset, request);
        asset = assetRepository.save(asset);
        return toDTO(asset);
    }

    @Transactional
    public void delete(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));
        asset.setDeletedAt(LocalDateTime.now());
        assetRepository.save(asset);
    }

    @Transactional
    public AssetDTO changeStatus(Long id, AssetStatus newStatus, String reason) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));

        asset.setStatus(newStatus);

        if (newStatus == AssetStatus.IN_SERVICE && asset.getServiceStartDate() == null) {
            asset.setServiceStartDate(java.time.LocalDate.now());
        }

        if (newStatus == AssetStatus.DISPOSED || newStatus == AssetStatus.RETIRED) {
            asset.setExitDate(java.time.LocalDate.now());
            asset.setExitReason(reason);
        }

        asset = assetRepository.save(asset);
        return toDTO(asset);
    }

    private void setRelations(Asset asset, AssetRequest request) {
        if (request.getSupplierId() != null) {
            asset.setSupplier(supplierRepository.findById(request.getSupplierId()).orElse(null));
        }
        if (request.getProjectId() != null) {
            asset.setProject(projectRepository.findById(request.getProjectId()).orElse(null));
        }
        if (request.getAssetTypeId() != null) {
            asset.setAssetType(assetTypeRepository.findById(request.getAssetTypeId()).orElse(null));
        }
        if (request.getLocationId() != null) {
            asset.setLocation(locationRepository.findById(request.getLocationId()).orElse(null));
        }
    }

    private AssetDTO toDTO(Asset asset) {
        return AssetDTO.builder()
                .id(asset.getId())
                .cabNumber(asset.getCabNumber())
                .projectCode(asset.getProjectCode())
                .assetNumber(asset.getAssetNumber())
                .designation(asset.getDesignation())
                .serialNumber(asset.getSerialNumber())
                .quantity(asset.getQuantity())
                .unitPrice(asset.getUnitPrice())
                .totalValue(asset.getTotalValue())
                .deliveryDate(asset.getDeliveryDate())
                .status(asset.getStatus())
                .supplierName(asset.getSupplier() != null ? asset.getSupplier().getName() : null)
                .projectName(asset.getProject() != null ? asset.getProject().getName() : null)
                .assetTypeName(asset.getAssetType() != null ? asset.getAssetType().getName() : null)
                .locationName(asset.getLocation() != null ? asset.getLocation().getName() : null)
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}

