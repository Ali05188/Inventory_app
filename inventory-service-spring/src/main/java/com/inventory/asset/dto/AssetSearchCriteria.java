package com.inventory.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetSearchCriteria {
    private String search;
    private Long projectId;
    private Long supplierId;
    private String status;
    private String dateFrom;
    private String dateTo;
}

