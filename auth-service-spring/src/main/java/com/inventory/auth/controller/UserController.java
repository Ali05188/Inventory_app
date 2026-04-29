package com.inventory.auth.controller;

import com.inventory.auth.dto.CreateUserRequest;
import com.inventory.auth.dto.UpdateUserRequest;
import com.inventory.auth.dto.UserResponse;
import com.inventory.auth.entity.Role;
import com.inventory.auth.entity.User;
import com.inventory.auth.repository.UserRepository;
import com.inventory.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view users')")
    @Operation(summary = "Get all users", description = "Retrieve paginated list of all users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view users')")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create users')")
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit users')")
    @Operation(summary = "Update user", description = "Update an existing user")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete users')")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        userService.deleteUser(id, currentUser.getId());
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('view users')")
    @Operation(summary = "Get all roles", description = "Retrieve list of all available roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}

