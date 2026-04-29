package com.inventory.auth.config;

import com.inventory.auth.entity.Permission;
import com.inventory.auth.entity.Role;
import com.inventory.auth.entity.User;
import com.inventory.auth.repository.PermissionRepository;
import com.inventory.auth.repository.RoleRepository;
import com.inventory.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initializing default data...");

        // Create permissions
        List<String> permissionNames = Arrays.asList(
            "view dashboard",
            "view assets", "create assets", "edit assets", "delete assets", "change asset status",
            "view users", "create users", "edit users", "delete users",
            "view suppliers", "create suppliers", "edit suppliers", "delete suppliers",
            "view projects", "create projects", "edit projects", "delete projects",
            "view locations", "create locations", "edit locations", "delete locations",
            "view reports", "export reports"
        );

        for (String permissionName : permissionNames) {
            if (!permissionRepository.existsByName(permissionName)) {
                Permission permission = Permission.builder()
                        .name(permissionName)
                        .guardName("web")
                        .build();
                permissionRepository.save(permission);
                log.info("Created permission: {}", permissionName);
            }
        }

        // Create roles with permissions
        createRoleWithPermissions("Super Admin", permissionNames);

        createRoleWithPermissions("Asset Manager", Arrays.asList(
            "view dashboard", "view assets", "create assets", "edit assets", "delete assets",
            "change asset status", "view suppliers", "view projects", "view locations", "view reports"
        ));

        createRoleWithPermissions("Auditor", Arrays.asList(
            "view dashboard", "view assets", "view suppliers", "view projects",
            "view locations", "view reports", "export reports"
        ));

        createRoleWithPermissions("Finance", Arrays.asList(
            "view dashboard", "view assets", "view reports", "export reports"
        ));

        createRoleWithPermissions("Viewer", Arrays.asList(
            "view dashboard", "view assets"
        ));

        // Create default admin user
        if (!userRepository.existsByEmail("admin@inventory.com")) {
            Role superAdminRole = roleRepository.findByName("Super Admin")
                    .orElseThrow(() -> new RuntimeException("Super Admin role not found"));

            User admin = User.builder()
                    .name("Administrator")
                    .email("admin@inventory.com")
                    .password(passwordEncoder.encode("password123"))
                    .isAdmin(true)
                    .roles(new HashSet<>())
                    .build();
            admin.addRole(superAdminRole);
            userRepository.save(admin);
            log.info("Created default admin user: admin@inventory.com");
        }

        log.info("Data initialization completed!");
    }

    private void createRoleWithPermissions(String roleName, List<String> permissionNames) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .guardName("web")
                    .permissions(new HashSet<>())
                    .build();

            for (String permissionName : permissionNames) {
                permissionRepository.findByName(permissionName).ifPresent(role::addPermission);
            }

            roleRepository.save(role);
            log.info("Created role: {} with {} permissions", roleName, permissionNames.size());
        }
    }
}

