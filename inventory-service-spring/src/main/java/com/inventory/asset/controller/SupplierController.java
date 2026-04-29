package com.inventory.asset.controller;

import com.inventory.asset.entity.Supplier;
import com.inventory.asset.repository.SupplierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Supplier management APIs")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view suppliers') or hasAuthority('view assets')")
    @Operation(summary = "Get all suppliers", description = "Retrieve list of all suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierRepository.findAll());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('view suppliers') or hasAuthority('view assets')")
    @Operation(summary = "Get active suppliers", description = "Retrieve list of active suppliers only")
    public ResponseEntity<List<Supplier>> getActiveSuppliers() {
        return ResponseEntity.ok(supplierRepository.findByIsActiveTrue());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view suppliers')")
    @Operation(summary = "Get supplier by ID", description = "Retrieve a specific supplier")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return ResponseEntity.ok(supplier);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create suppliers')")
    @Operation(summary = "Create supplier", description = "Create a new supplier")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        if (supplierRepository.existsByCode(supplier.getCode())) {
            throw new RuntimeException("Supplier code already exists");
        }
        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit suppliers')")
    @Operation(summary = "Update supplier", description = "Update an existing supplier")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplier.setCode(supplierDetails.getCode());
        supplier.setName(supplierDetails.getName());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setPhone(supplierDetails.getPhone());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setIsActive(supplierDetails.getIsActive());

        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete suppliers')")
    @Operation(summary = "Delete supplier", description = "Delete a supplier")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        supplierRepository.delete(supplier);
        return ResponseEntity.ok("Supplier deleted successfully");
    }
}

