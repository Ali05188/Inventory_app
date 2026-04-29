package com.inventory.asset.service;

import com.inventory.asset.dto.AssetAssignmentDTO;
import com.inventory.asset.dto.AssetAssignmentRequest;
import com.inventory.asset.entity.Asset;
import com.inventory.asset.entity.AssetAssignment;
import com.inventory.asset.repository.AssetAssignmentRepository;
import com.inventory.asset.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetAssignmentServiceTest {

    @Mock
    private AssetAssignmentRepository assignmentRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetAssignmentService assignmentService;

    @BeforeEach
    void setUp() {}

    @Test
    void testAssignAsset_Success() {
        // Arrange
        Long assetId = 1L;
        Asset asset = Asset.builder().id(assetId).designation("Test Laptop").build();
        AssetAssignmentRequest request = new AssetAssignmentRequest();
        request.setAssetId(assetId);
        request.setAssignedToUserId(10L);
        request.setAssignmentDate(LocalDate.now());
        request.setNotes("Assigned new developer");

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        AssetAssignment savedAssignment = AssetAssignment.builder()
                .id(100L)
                .asset(asset)
                .assignedToUserId(10L)
                .assignmentDate(LocalDate.now())
                .status("ACTIVE")
                .notes("Assigned new developer")
                .build();

        when(assignmentRepository.save(any(AssetAssignment.class))).thenReturn(savedAssignment);

        // Act
        AssetAssignmentDTO result = assignmentService.assignAsset(request);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(10L, result.getAssignedToUserId());
        verify(assetRepository, times(1)).findById(assetId);
        verify(assignmentRepository, times(1)).save(any(AssetAssignment.class));
    }

    @Test
    void testAssignAsset_AssetNotFound() {
        // Arrange
        AssetAssignmentRequest request = new AssetAssignmentRequest();
        request.setAssetId(99L);

        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> assignmentService.assignAsset(request));
        assertEquals("Asset not found", exception.getMessage());
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void testReturnAsset_Success() {
        // Arrange
        Long assignmentId = 100L;
        Asset asset = Asset.builder().id(1L).build();
        AssetAssignment assignment = AssetAssignment.builder()
                .id(assignmentId)
                .asset(asset)
                .assignedToUserId(10L)
                .status("ACTIVE")
                .notes("Original note")
                .build();

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        AssetAssignment returnedAssignment = AssetAssignment.builder()
                .id(assignmentId)
                .asset(asset)
                .assignedToUserId(10L)
                .status("RETURNED")
                .notes("Original note\nReturn notes: Returned in good condition")
                .returnDate(LocalDate.now())
                .build();

        when(assignmentRepository.save(any(AssetAssignment.class))).thenReturn(returnedAssignment);

        // Act
        AssetAssignmentDTO result = assignmentService.returnAsset(assignmentId, LocalDate.now(), "Returned in good condition");

        // Assert
        assertNotNull(result);
        assertEquals("RETURNED", result.getStatus());
        assertTrue(result.getNotes().contains("Returned in good condition"));
        assertNotNull(result.getReturnDate());
        verify(assignmentRepository, times(1)).findById(assignmentId);
        verify(assignmentRepository, times(1)).save(any(AssetAssignment.class));
    }
}

