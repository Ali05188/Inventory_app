package com.inventory.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetRequest {

    @NotBlank(message = "CAB number is required")
    private String cabNumber;

    private String projectCode;

    @NotBlank(message = "Asset number is required")
    private String assetNumber;

    @NotBlank(message = "Designation is required")
    private String designation;

    private String serialNumber;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private BigDecimal unitPrice;
    private LocalDate deliveryDate;

    private Long supplierId;
    private Long projectId;
    private Long assetTypeId;
    private Long locationId;
}

