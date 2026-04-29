package com.inventory.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.asset.dto.CreateAssetRequest;
import com.inventory.asset.entity.*;
import com.inventory.asset.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    private Supplier testSupplier;
    private Project testProject;
    private Location testLocation;
    private AssetType testAssetType;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        // Create test data
        testSupplier = supplierRepository.save(Supplier.builder()
                .code("TEST-SUP")
                .name("Test Supplier")
                .email("test@supplier.com")
                .isActive(true)
                .build());

        testProject = projectRepository.save(Project.builder()
                .code("TEST-PROJ")
                .name("Test Project")
                .status("active")
                .build());

        testLocation = locationRepository.save(Location.builder()
                .code("TEST-LOC")
                .name("Test Location")
                .building("Test Building")
                .floor("1")
                .room("101")
                .isActive(true)
                .build());

        testAssetType = assetTypeRepository.save(AssetType.builder()
                .name("Test Type")
                .description("Test asset type")
                .build());

        testAsset = assetRepository.save(Asset.builder()
                .cabNumber("TEST-CAB-001")
                .assetNumber("TEST-AST-001")
                .designation("Test Laptop")
                .serialNumber("TEST-SN-001")
                .quantity(1)
                .unitPrice(new BigDecimal("999.99"))
                .deliveryDate(LocalDate.now())
                .status("new")
                .supplier(testSupplier)
                .project(testProject)
                .location(testLocation)
                .assetType(testAssetType)
                .build());
    }

    @Test
    @WithMockUser(authorities = {"view assets"})
    void getAllAssets_ReturnsPagedAssets() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = {"view assets"})
    void getAssetById_ExistingAsset_ReturnsAsset() throws Exception {
        mockMvc.perform(get("/api/assets/{id}", testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAsset.getId()))
                .andExpect(jsonPath("$.cabNumber").value("TEST-CAB-001"))
                .andExpect(jsonPath("$.designation").value("Test Laptop"))
                .andExpect(jsonPath("$.status").value("new"));
    }

    @Test
    @WithMockUser(authorities = {"view assets"})
    void getAssetById_NonExistingAsset_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/assets/{id}", 99999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    @WithMockUser(authorities = {"create assets"})
    void createAsset_ValidData_ReturnsCreatedAsset() throws Exception {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setCabNumber("NEW-CAB-001");
        request.setAssetNumber("NEW-AST-001");
        request.setDesignation("New Test Asset");
        request.setSerialNumber("NEW-SN-001");
        request.setQuantity(1);
        request.setUnitPrice(new BigDecimal("500.00"));
        request.setSupplierId(testSupplier.getId());
        request.setProjectId(testProject.getId());

        mockMvc.perform(post("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cabNumber").value("NEW-CAB-001"))
                .andExpect(jsonPath("$.designation").value("New Test Asset"))
                .andExpect(jsonPath("$.status").value("new"));
    }

    @Test
    @WithMockUser(authorities = {"create assets"})
    void createAsset_DuplicateSerialNumber_ReturnsBadRequest() throws Exception {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setCabNumber("NEW-CAB-002");
        request.setAssetNumber("NEW-AST-002");
        request.setDesignation("Another Asset");
        request.setSerialNumber("TEST-SN-001"); // Duplicate serial number
        request.setQuantity(1);

        mockMvc.perform(post("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Serial number already exists")));
    }

    @Test
    @WithMockUser(authorities = {"edit assets"})
    void updateAsset_ValidData_ReturnsUpdatedAsset() throws Exception {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setCabNumber("UPDATED-CAB");
        request.setAssetNumber("UPDATED-AST");
        request.setDesignation("Updated Designation");
        request.setSerialNumber("TEST-SN-001");
        request.setQuantity(2);
        request.setUnitPrice(new BigDecimal("1500.00"));

        mockMvc.perform(put("/api/assets/{id}", testAsset.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cabNumber").value("UPDATED-CAB"))
                .andExpect(jsonPath("$.designation").value("Updated Designation"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(authorities = {"delete assets"})
    void deleteAsset_ExistingAsset_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/assets/{id}", testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Asset deleted successfully"));
    }

    @Test
    @WithMockUser(authorities = {"change asset status"})
    void changeStatus_ValidTransition_ReturnsUpdatedAsset() throws Exception {
        String requestBody = "{\"newStatus\": \"in_service\", \"reason\": \"Asset deployed\"}";

        mockMvc.perform(post("/api/assets/{id}/change-status", testAsset.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("in_service"));
    }

    @Test
    @WithMockUser(authorities = {"change asset status"})
    void changeStatus_InvalidTransition_ReturnsBadRequest() throws Exception {
        String requestBody = "{\"newStatus\": \"maintenance\", \"reason\": \"Invalid transition\"}";

        mockMvc.perform(post("/api/assets/{id}/change-status", testAsset.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid status transition")));
    }

    @Test
    @WithMockUser(authorities = {"view assets"})
    void getAllowedTransitions_ReturnsTransitions() throws Exception {
        mockMvc.perform(get("/api/assets/{id}/allowed-transitions", testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasItem("in_service")))
                .andExpect(jsonPath("$", hasItem("disposed")));
    }

    @Test
    void getAllAssets_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {})
    void getAllAssets_WithoutPermission_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isForbidden());
    }
}

