package com.inventory.asset.controller;

import com.inventory.asset.entity.Location;
import com.inventory.asset.repository.LocationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Location Management", description = "Location management APIs")
public class LocationController {

    private final LocationRepository locationRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view locations') or hasAuthority('view assets')")
    @Operation(summary = "Get all locations", description = "Retrieve list of all locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('view locations') or hasAuthority('view assets')")
    @Operation(summary = "Get active locations", description = "Retrieve list of active locations only")
    public ResponseEntity<List<Location>> getActiveLocations() {
        return ResponseEntity.ok(locationRepository.findByIsActiveTrue());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view locations')")
    @Operation(summary = "Get location by ID", description = "Retrieve a specific location")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return ResponseEntity.ok(location);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create locations')")
    @Operation(summary = "Create location", description = "Create a new location")
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        if (locationRepository.existsByCode(location.getCode())) {
            throw new RuntimeException("Location code already exists");
        }
        return ResponseEntity.ok(locationRepository.save(location));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit locations')")
    @Operation(summary = "Update location", description = "Update an existing location")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location locationDetails) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        location.setCode(locationDetails.getCode());
        location.setName(locationDetails.getName());
        location.setBuilding(locationDetails.getBuilding());
        location.setFloor(locationDetails.getFloor());
        location.setRoom(locationDetails.getRoom());
        location.setDescription(locationDetails.getDescription());
        location.setIsActive(locationDetails.getIsActive());

        return ResponseEntity.ok(locationRepository.save(location));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete locations')")
    @Operation(summary = "Delete location", description = "Delete a location")
    public ResponseEntity<String> deleteLocation(@PathVariable Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        locationRepository.delete(location);
        return ResponseEntity.ok("Location deleted successfully");
    }
}

