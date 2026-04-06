package com.inventory.asset.controller;

import com.inventory.asset.dto.MaintenanceTicketDTO;
import com.inventory.asset.dto.MaintenanceTicketRequest;
import com.inventory.asset.service.MaintenanceTicketService;
import com.inventory.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceTicketController {

    private final MaintenanceTicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceTicketDTO>> createTicket(@Valid @RequestBody MaintenanceTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Maintenance ticket created", ticketService.createTicket(request)));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<MaintenanceTicketDTO>> resolveTicket(
            @PathVariable Long id,
            @RequestParam String resolutionNotes
    ) {
        return ResponseEntity.ok(ApiResponse.success("Ticket resolved successfully",
                ticketService.resolveTicket(id, resolutionNotes)));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<ApiResponse<List<MaintenanceTicketDTO>>> getByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTicketsByAsset(assetId)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MaintenanceTicketDTO>>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTicketsByStatus(status)));
    }
}

