package com.inventory.asset.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssetRequest {

    @NotBlank(message = "CAB number is required")
    @Size(max = 255, message = "CAB number must not exceed 255 characters")
    private String cabNumber;

    @NotBlank(message = "Asset number is required")
    @Size(max = 255, message = "Asset number must not exceed 255 characters")
    private String assetNumber;

    @NotBlank(message = "Designation is required")
    @Size(max = 255, message = "Designation must not exceed 255 characters")
    private String designation;

    @Size(max = 255, message = "Serial number must not exceed 255 characters")
    private String serialNumber;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;

    private LocalDate deliveryDate;

    private Long projectId;
    private Long supplierId;
    private Long locationId;
    private Long assetTypeId;
}

