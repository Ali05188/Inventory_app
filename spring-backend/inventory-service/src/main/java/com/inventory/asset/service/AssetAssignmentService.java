package com.inventory.asset.service;

import com.inventory.asset.dto.AssetAssignmentDTO;
import com.inventory.asset.dto.AssetAssignmentRequest;
import com.inventory.asset.entity.Asset;
import com.inventory.asset.entity.AssetAssignment;
import com.inventory.asset.repository.AssetAssignmentRepository;
import com.inventory.asset.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetAssignmentService {

    private final AssetAssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public AssetAssignmentDTO assignAsset(AssetAssignmentRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // Here we could also change asset status to e.g. IN_USE
        // asset.setStatus(AssetStatus.IN_USE);
        // assetRepository.save(asset);

        AssetAssignment assignment = AssetAssignment.builder()
                .asset(asset)
                .assignedToUserId(request.getAssignedToUserId())
                .assignmentDate(request.getAssignmentDate())
                .notes(request.getNotes())
                .status("ACTIVE")
                .build();

        AssetAssignment saved = assignmentRepository.save(assignment);
        return mapToDTO(saved);
    }

    @Transactional
    public AssetAssignmentDTO returnAsset(Long id, LocalDate returnDate, String notes) {
        AssetAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setReturnDate(returnDate != null ? returnDate : LocalDate.now());
        assignment.setStatus("RETURNED");
        if (notes != null) {
            assignment.setNotes(assignment.getNotes() + "\nReturn notes: " + notes);
        }

        // Could also reset Asset status to AVAILABLE/PENDING
        return mapToDTO(assignmentRepository.save(assignment));
    }

    public List<AssetAssignmentDTO> getAssignmentsByAsset(Long assetId) {
        return assignmentRepository.findByAssetId(assetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AssetAssignmentDTO> getAssignmentsByUser(Long userId) {
        return assignmentRepository.findByAssignedToUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AssetAssignmentDTO mapToDTO(AssetAssignment assignment) {
        return AssetAssignmentDTO.builder()
                .id(assignment.getId())
                .assetId(assignment.getAsset().getId())
                .assignedToUserId(assignment.getAssignedToUserId())
                .assignmentDate(assignment.getAssignmentDate())
                .returnDate(assignment.getReturnDate())
                .notes(assignment.getNotes())
                .status(assignment.getStatus())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}

