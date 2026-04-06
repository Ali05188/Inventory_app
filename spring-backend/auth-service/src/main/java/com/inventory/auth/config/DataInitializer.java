package com.inventory.auth.config;

import com.inventory.auth.entity.Permission;
import com.inventory.auth.entity.Role;
import com.inventory.auth.entity.User;
import com.inventory.auth.repository.RoleRepository;
import com.inventory.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role superAdmin = new Role();
            superAdmin.setName("SUPER_ADMIN");
            roleRepository.save(superAdmin);

            Role viewer = new Role();
            viewer.setName("VIEWER");
            roleRepository.save(viewer);

            if (userRepository.count() == 0) {
                User adminUser = new User();
                adminUser.setName("Super Admin");
                adminUser.setEmail("admin@email.com");
                adminUser.setPassword(passwordEncoder.encode("password123"));
                adminUser.setActive(true);
                Set<Role> roles = new HashSet<>();
                roles.add(superAdmin);
                adminUser.setRoles(roles);

                userRepository.save(adminUser);
            }
        }
    }
}

