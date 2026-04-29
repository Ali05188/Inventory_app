package com.inventory.asset.controller;

import com.inventory.asset.dto.AssetAssignmentDTO;
import com.inventory.asset.dto.AssetAssignmentRequest;
import com.inventory.asset.service.AssetAssignmentService;
import com.inventory.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssetAssignmentController {

    private final AssetAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssetAssignmentDTO>> createAssignment(@Valid @RequestBody AssetAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asset assigned successfully", assignmentService.assignAsset(request)));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<AssetAssignmentDTO>> returnAsset(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate returnDate,
            @RequestParam(required = false) String notes
    ) {
        return ResponseEntity.ok(ApiResponse.success("Asset returned successfully",
                assignmentService.returnAsset(id, returnDate, notes)));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<ApiResponse<List<AssetAssignmentDTO>>> getByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAssignmentsByAsset(assetId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AssetAssignmentDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAssignmentsByUser(userId)));
    }
}

