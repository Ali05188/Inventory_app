package com.inventory.asset.dto;

import com.inventory.common.enums.AssetStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private Long id;
    private String cabNumber;
    private String projectCode;
    private String assetNumber;
    private String designation;
    private String serialNumber;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private LocalDate deliveryDate;
    private AssetStatus status;
    private String supplierName;
    private String projectName;
    private String assetTypeName;
    private String locationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

