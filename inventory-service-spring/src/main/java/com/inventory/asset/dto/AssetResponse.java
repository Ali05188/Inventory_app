package com.inventory.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {

    private Long id;
    private String cabNumber;
    private String projectCode;
    private String assetNumber;
    private String designation;
    private String serialNumber;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDate deliveryDate;
    private String status;
    private LocalDate serviceStartDate;
    private LocalDate exitDate;
    private String exitReason;

    private SupplierResponse supplier;
    private ProjectResponse project;
    private LocationResponse location;
    private AssetTypeResponse assetType;
    private List<StatusHistoryResponse> statusHistories;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierResponse {
        private Long id;
        private String code;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponse {
        private Long id;
        private String code;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationResponse {
        private Long id;
        private String code;
        private String name;
        private String building;
        private String floor;
        private String room;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetTypeResponse {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHistoryResponse {
        private Long id;
        private String oldStatus;
        private String newStatus;
        private String reason;
        private String changedByName;
        private LocalDateTime createdAt;
    }
}

