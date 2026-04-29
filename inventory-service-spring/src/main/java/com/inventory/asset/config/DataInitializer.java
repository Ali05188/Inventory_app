package com.inventory.asset.config;

import com.inventory.asset.entity.*;
import com.inventory.asset.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final ProjectRepository projectRepository;
    private final LocationRepository locationRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetRepository assetRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initializing sample data for inventory service...");

        // Create sample suppliers
        if (supplierRepository.count() == 0) {
            supplierRepository.save(Supplier.builder()
                    .code("SUP001")
                    .name("Dell Technologies")
                    .email("sales@dell.com")
                    .phone("+1-800-999-3355")
                    .address("Round Rock, Texas, USA")
                    .isActive(true)
                    .build());

            supplierRepository.save(Supplier.builder()
                    .code("SUP002")
                    .name("HP Inc.")
                    .email("sales@hp.com")
                    .phone("+1-800-474-6836")
                    .address("Palo Alto, California, USA")
                    .isActive(true)
                    .build());

            supplierRepository.save(Supplier.builder()
                    .code("SUP003")
                    .name("Lenovo")
                    .email("sales@lenovo.com")
                    .phone("+1-855-253-6686")
                    .address("Morrisville, North Carolina, USA")
                    .isActive(true)
                    .build());

            log.info("Created sample suppliers");
        }

        // Create sample projects
        if (projectRepository.count() == 0) {
            projectRepository.save(Project.builder()
                    .code("PROJ001")
                    .name("IT Infrastructure Upgrade")
                    .description("Company-wide IT infrastructure modernization")
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2026, 12, 31))
                    .status("active")
                    .build());

            projectRepository.save(Project.builder()
                    .code("PROJ002")
                    .name("Office Expansion")
                    .description("New office equipment for expansion")
                    .startDate(LocalDate.of(2026, 3, 1))
                    .endDate(LocalDate.of(2026, 6, 30))
                    .status("active")
                    .build());

            log.info("Created sample projects");
        }

        // Create sample locations
        if (locationRepository.count() == 0) {
            locationRepository.save(Location.builder()
                    .code("LOC001")
                    .name("Main Office - Floor 1")
                    .building("Main Building")
                    .floor("1")
                    .room("101")
                    .description("Reception and lobby area")
                    .isActive(true)
                    .build());

            locationRepository.save(Location.builder()
                    .code("LOC002")
                    .name("Main Office - Floor 2")
                    .building("Main Building")
                    .floor("2")
                    .room("201")
                    .description("Development team workspace")
                    .isActive(true)
                    .build());

            locationRepository.save(Location.builder()
                    .code("LOC003")
                    .name("Data Center")
                    .building("Tech Building")
                    .floor("B1")
                    .room("DC01")
                    .description("Main data center")
                    .isActive(true)
                    .build());

            log.info("Created sample locations");
        }

        // Create sample asset types
        if (assetTypeRepository.count() == 0) {
            assetTypeRepository.save(AssetType.builder()
                    .name("Laptop")
                    .description("Portable computer")
                    .build());

            assetTypeRepository.save(AssetType.builder()
                    .name("Desktop")
                    .description("Desktop computer workstation")
                    .build());

            assetTypeRepository.save(AssetType.builder()
                    .name("Monitor")
                    .description("Computer display monitor")
                    .build());

            assetTypeRepository.save(AssetType.builder()
                    .name("Server")
                    .description("Server hardware")
                    .build());

            assetTypeRepository.save(AssetType.builder()
                    .name("Network Equipment")
                    .description("Switches, routers, and network devices")
                    .build());

            log.info("Created sample asset types");
        }

        // Create sample assets
        if (assetRepository.count() == 0) {
            Supplier dell = supplierRepository.findByCode("SUP001").orElse(null);
            Project itProject = projectRepository.findByCode("PROJ001").orElse(null);
            Location devFloor = locationRepository.findByCode("LOC002").orElse(null);
            AssetType laptop = assetTypeRepository.findByName("Laptop").orElse(null);

            if (dell != null && itProject != null && devFloor != null && laptop != null) {
                assetRepository.save(Asset.builder()
                        .cabNumber("CAB-2026-001")
                        .assetNumber("AST-001")
                        .projectCode(itProject.getCode())
                        .designation("Dell Latitude 5540")
                        .serialNumber("DELL-LAT-5540-001")
                        .quantity(1)
                        .unitPrice(new BigDecimal("1299.99"))
                        .deliveryDate(LocalDate.of(2026, 1, 15))
                        .status("in_service")
                        .serviceStartDate(LocalDate.of(2026, 1, 20))
                        .supplier(dell)
                        .project(itProject)
                        .location(devFloor)
                        .assetType(laptop)
                        .build());

                assetRepository.save(Asset.builder()
                        .cabNumber("CAB-2026-002")
                        .assetNumber("AST-002")
                        .projectCode(itProject.getCode())
                        .designation("Dell Latitude 5540")
                        .serialNumber("DELL-LAT-5540-002")
                        .quantity(1)
                        .unitPrice(new BigDecimal("1299.99"))
                        .deliveryDate(LocalDate.of(2026, 1, 15))
                        .status("new")
                        .supplier(dell)
                        .project(itProject)
                        .location(devFloor)
                        .assetType(laptop)
                        .build());

                log.info("Created sample assets");
            }
        }

        log.info("Data initialization completed!");
    }
}

