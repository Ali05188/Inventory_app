package com.inventory.asset.service;

import com.inventory.asset.dto.MaintenanceTicketDTO;
import com.inventory.asset.dto.MaintenanceTicketRequest;
import com.inventory.asset.entity.Asset;
import com.inventory.asset.entity.MaintenanceTicket;
import com.inventory.asset.repository.AssetRepository;
import com.inventory.asset.repository.MaintenanceTicketRepository;
import com.inventory.common.enums.AssetStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceTicketService {

    private final MaintenanceTicketRepository ticketRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public MaintenanceTicketDTO createTicket(MaintenanceTicketRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        MaintenanceTicket ticket = MaintenanceTicket.builder()
                .asset(asset)
                .issueDescription(request.getIssueDescription())
                .priority(request.getPriority() != null ? request.getPriority().toUpperCase() : "MEDIUM")
                .status("OPEN")
                .reportedDate(LocalDate.now())
                .build();

        // Optionally update asset status automatically based on severe maintenance needed
        // asset.setStatus(AssetStatus.IN_MAINTENANCE);
        // assetRepository.save(asset);

        return mapToDTO(ticketRepository.save(ticket));
    }

    @Transactional
    public MaintenanceTicketDTO resolveTicket(Long ticketId, String resolutionNotes) {
        MaintenanceTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus("RESOLVED");
        ticket.setResolvedDate(LocalDate.now());
        ticket.setResolutionNotes(resolutionNotes);

        return mapToDTO(ticketRepository.save(ticket));
    }

    public List<MaintenanceTicketDTO> getTicketsByAsset(Long assetId) {
        return ticketRepository.findByAssetId(assetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceTicketDTO> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MaintenanceTicketDTO mapToDTO(MaintenanceTicket ticket) {
        return MaintenanceTicketDTO.builder()
                .id(ticket.getId())
                .assetId(ticket.getAsset().getId())
                .issueDescription(ticket.getIssueDescription())
                .resolutionNotes(ticket.getResolutionNotes())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .reportedDate(ticket.getReportedDate())
                .resolvedDate(ticket.getResolvedDate())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}

