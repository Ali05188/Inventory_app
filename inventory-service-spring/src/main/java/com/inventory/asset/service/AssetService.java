package com.inventory.asset.service;

import com.inventory.asset.dto.*;
import com.inventory.asset.entity.*;
import com.inventory.asset.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final SupplierRepository supplierRepository;
    private final ProjectRepository projectRepository;
    private final LocationRepository locationRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetLifecycleService lifecycleService;

    public Page<AssetResponse> getAllAssets(AssetSearchCriteria criteria, Pageable pageable) {
        Specification<Asset> spec = buildSpecification(criteria);
        return assetRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    public AssetResponse getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));
        return mapToResponse(asset);
    }

    @Transactional
    public AssetResponse createAsset(CreateAssetRequest request) {
        // Validate unique serial number
        if (request.getSerialNumber() != null &&
            assetRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new RuntimeException("Serial number already exists");
        }

        Asset asset = Asset.builder()
                .cabNumber(request.getCabNumber())
                .assetNumber(request.getAssetNumber())
                .designation(request.getDesignation())
                .serialNumber(request.getSerialNumber())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .deliveryDate(request.getDeliveryDate())
                .status(AssetLifecycleService.STATUS_NEW)
                .build();

        // Set relationships
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            asset.setSupplier(supplier);
        }

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            asset.setProject(project);
            asset.setProjectCode(project.getCode());
        }

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            asset.setLocation(location);
        }

        if (request.getAssetTypeId() != null) {
            AssetType assetType = assetTypeRepository.findById(request.getAssetTypeId())
                    .orElseThrow(() -> new RuntimeException("Asset type not found"));
            asset.setAssetType(assetType);
        }

        asset = assetRepository.save(asset);
        return mapToResponse(asset);
    }

    @Transactional
    public AssetResponse updateAsset(Long id, CreateAssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));

        // Validate unique serial number
        if (request.getSerialNumber() != null &&
            assetRepository.existsBySerialNumberAndIdNot(request.getSerialNumber(), id)) {
            throw new RuntimeException("Serial number already exists");
        }

        asset.setCabNumber(request.getCabNumber());
        asset.setAssetNumber(request.getAssetNumber());
        asset.setDesignation(request.getDesignation());
        asset.setSerialNumber(request.getSerialNumber());
        asset.setQuantity(request.getQuantity());
        asset.setUnitPrice(request.getUnitPrice());
        asset.setDeliveryDate(request.getDeliveryDate());

        // Update relationships
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            asset.setSupplier(supplier);
        } else {
            asset.setSupplier(null);
        }

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            asset.setProject(project);
            asset.setProjectCode(project.getCode());
        } else {
            asset.setProject(null);
            asset.setProjectCode(null);
        }

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            asset.setLocation(location);
        } else {
            asset.setLocation(null);
        }

        if (request.getAssetTypeId() != null) {
            AssetType assetType = assetTypeRepository.findById(request.getAssetTypeId())
                    .orElseThrow(() -> new RuntimeException("Asset type not found"));
            asset.setAssetType(assetType);
        } else {
            asset.setAssetType(null);
        }

        asset = assetRepository.save(asset);
        return mapToResponse(asset);
    }

    @Transactional
    public AssetResponse changeStatus(Long id, ChangeStatusRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));

        String oldStatus = asset.getStatus();
        String newStatus = request.getNewStatus();

        // Validate status transition
        lifecycleService.validateTransition(oldStatus, newStatus);

        // Create status history
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AssetStatusHistory history = AssetStatusHistory.builder()
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(request.getReason())
                .changedByName(currentUser)
                .build();

        asset.addStatusHistory(history);
        asset.setStatus(newStatus);

        // Set service start date if transitioning to in_service
        if (AssetLifecycleService.STATUS_IN_SERVICE.equals(newStatus) && asset.getServiceStartDate() == null) {
            asset.setServiceStartDate(LocalDate.now());
        }

        // Set exit date if decommissioned or disposed
        if (AssetLifecycleService.STATUS_DECOMMISSIONED.equals(newStatus) ||
            AssetLifecycleService.STATUS_DISPOSED.equals(newStatus)) {
            asset.setExitDate(LocalDate.now());
            asset.setExitReason(request.getReason());
        }

        asset = assetRepository.save(asset);
        return mapToResponse(asset);
    }

    @Transactional
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));

        // Soft delete
        asset.setDeletedAt(LocalDateTime.now());
        assetRepository.save(asset);
    }

    public List<String> getAllowedTransitions(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));

        if (asset.getStatus() == null) {
            return lifecycleService.getValidStatuses();
        }
        return lifecycleService.getAllowedTransitions(asset.getStatus());
    }

    private Specification<Asset> buildSpecification(AssetSearchCriteria criteria) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            // Exclude soft-deleted
            predicates = cb.and(predicates, cb.isNull(root.get("deletedAt")));

            if (criteria.getSearch() != null && !criteria.getSearch().isEmpty()) {
                String search = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                    cb.like(cb.lower(root.get("cabNumber")), search),
                    cb.like(cb.lower(root.get("assetNumber")), search),
                    cb.like(cb.lower(root.get("designation")), search),
                    cb.like(cb.lower(root.get("serialNumber")), search)
                ));
            }

            if (criteria.getProjectId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("project").get("id"), criteria.getProjectId()));
            }

            if (criteria.getSupplierId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("supplier").get("id"), criteria.getSupplierId()));
            }

            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (criteria.getDateFrom() != null && !criteria.getDateFrom().isEmpty()) {
                LocalDate dateFrom = LocalDate.parse(criteria.getDateFrom());
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("deliveryDate"), dateFrom));
            }

            if (criteria.getDateTo() != null && !criteria.getDateTo().isEmpty()) {
                LocalDate dateTo = LocalDate.parse(criteria.getDateTo());
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("deliveryDate"), dateTo));
            }

            return predicates;
        };
    }

    private AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .cabNumber(asset.getCabNumber())
                .projectCode(asset.getProjectCode())
                .assetNumber(asset.getAssetNumber())
                .designation(asset.getDesignation())
                .serialNumber(asset.getSerialNumber())
                .quantity(asset.getQuantity())
                .unitPrice(asset.getUnitPrice())
                .deliveryDate(asset.getDeliveryDate())
                .status(asset.getStatus())
                .serviceStartDate(asset.getServiceStartDate())
                .exitDate(asset.getExitDate())
                .exitReason(asset.getExitReason())
                .supplier(mapSupplier(asset.getSupplier()))
                .project(mapProject(asset.getProject()))
                .location(mapLocation(asset.getLocation()))
                .assetType(mapAssetType(asset.getAssetType()))
                .statusHistories(mapStatusHistories(asset.getStatusHistories()))
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }

    private AssetResponse.SupplierResponse mapSupplier(Supplier supplier) {
        if (supplier == null) return null;
        return AssetResponse.SupplierResponse.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .build();
    }

    private AssetResponse.ProjectResponse mapProject(Project project) {
        if (project == null) return null;
        return AssetResponse.ProjectResponse.builder()
                .id(project.getId())
                .code(project.getCode())
                .name(project.getName())
                .build();
    }

    private AssetResponse.LocationResponse mapLocation(Location location) {
        if (location == null) return null;
        return AssetResponse.LocationResponse.builder()
                .id(location.getId())
                .code(location.getCode())
                .name(location.getName())
                .building(location.getBuilding())
                .floor(location.getFloor())
                .room(location.getRoom())
                .build();
    }

    private AssetResponse.AssetTypeResponse mapAssetType(AssetType assetType) {
        if (assetType == null) return null;
        return AssetResponse.AssetTypeResponse.builder()
                .id(assetType.getId())
                .name(assetType.getName())
                .description(assetType.getDescription())
                .build();
    }

    private List<AssetResponse.StatusHistoryResponse> mapStatusHistories(List<AssetStatusHistory> histories) {
        if (histories == null) return null;
        return histories.stream()
                .map(h -> AssetResponse.StatusHistoryResponse.builder()
                        .id(h.getId())
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .reason(h.getReason())
                        .changedByName(h.getChangedByName())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}

