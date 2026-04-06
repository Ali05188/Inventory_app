package com.inventory.analytics.service;

import com.inventory.analytics.client.InventoryClient;
import com.inventory.analytics.dto.AssetDTO;
import com.inventory.analytics.dto.MaintenanceTicketDTO;
import com.inventory.analytics.dto.PredictiveMaintenanceResponse;
import com.inventory.common.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PredictiveMaintenanceServiceTest {

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private PredictiveMaintenanceService predictiveService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAnalyzeAsset_HighRisk() {
        // Arrange
        Long assetId = 1L;
        AssetDTO asset = AssetDTO.builder()
                .id(assetId)
                .designation("Old Server")
                .serialNumber("SN-12345")
                .serviceStartDate(LocalDate.now().minusDays(1800)) // almost 5 years old
                .build();

        MaintenanceTicketDTO ticket1 = MaintenanceTicketDTO.builder().priority("CRITICAL").build();
        MaintenanceTicketDTO ticket2 = MaintenanceTicketDTO.builder().priority("HIGH").build();
        MaintenanceTicketDTO ticket3 = MaintenanceTicketDTO.builder().priority("CRITICAL").build();

        when(inventoryClient.getAssetById(assetId)).thenReturn(ApiResponse.success(asset));
        when(inventoryClient.getTicketsByAsset(assetId)).thenReturn(ApiResponse.success(Arrays.asList(ticket1, ticket2, ticket3)));

        // Act
        PredictiveMaintenanceResponse response = predictiveService.analyzeAsset(assetId);

        // Assert
        assertNotNull(response);
        assertEquals(assetId, response.getAssetId());
        assertEquals("Old Server", response.getAssetDesignation());
        assertEquals(3, response.getTicketCount());

        // Age should penalize heavily (~39 points). Tickets: 15 + 10 + 15 = 40 points. Total ~79 points.
        assertTrue(response.getFailureProbability() >= 0.70); // at least 70% risk
        assertEquals("CRITICAL", response.getRiskLevel());
    }

    @Test
    void testAnalyzeAsset_LowRisk() {
        // Arrange
        Long assetId = 2L;
        AssetDTO asset = AssetDTO.builder()
                .id(assetId)
                .designation("New Laptop")
                .serialNumber("SN-99999")
                .serviceStartDate(LocalDate.now().minusDays(10)) // very new
                .build();

        when(inventoryClient.getAssetById(assetId)).thenReturn(ApiResponse.success(asset));
        when(inventoryClient.getTicketsByAsset(assetId)).thenReturn(ApiResponse.success(List.of()));

        // Act
        PredictiveMaintenanceResponse response = predictiveService.analyzeAsset(assetId);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTicketCount());
        assertEquals("LOW", response.getRiskLevel()); // Should be less than 20% risk
        assertTrue(response.getFailureProbability() < 0.20);
    }
}

