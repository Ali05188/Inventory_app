package com.inventory.asset.controller;

import com.inventory.asset.entity.Project;
import com.inventory.asset.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "Project management APIs")
public class ProjectController {

    private final ProjectRepository projectRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view projects') or hasAuthority('view assets')")
    @Operation(summary = "Get all projects", description = "Retrieve list of all projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view projects')")
    @Operation(summary = "Get project by ID", description = "Retrieve a specific project")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return ResponseEntity.ok(project);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create projects')")
    @Operation(summary = "Create project", description = "Create a new project")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        if (projectRepository.existsByCode(project.getCode())) {
            throw new RuntimeException("Project code already exists");
        }
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit projects')")
    @Operation(summary = "Update project", description = "Update an existing project")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setCode(projectDetails.getCode());
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setStartDate(projectDetails.getStartDate());
        project.setEndDate(projectDetails.getEndDate());
        project.setStatus(projectDetails.getStatus());

        return ResponseEntity.ok(projectRepository.save(project));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete projects')")
    @Operation(summary = "Delete project", description = "Delete a project")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        projectRepository.delete(project);
        return ResponseEntity.ok("Project deleted successfully");
    }
}

