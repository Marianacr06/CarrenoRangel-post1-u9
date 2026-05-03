package com.carrenorangel.authdemo.config;

import com.carrenorangel.authdemo.entity.AppUser;
import com.carrenorangel.authdemo.entity.Role;
import com.carrenorangel.authdemo.entity.RoleName;
import com.carrenorangel.authdemo.repository.AppUserRepository;
import com.carrenorangel.authdemo.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.findByName(RoleName.USER).isEmpty()) {
                Role userRole = new Role();
                userRole.setName(RoleName.USER);
                roleRepository.save(userRole);
            }
            if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName(RoleName.ADMIN);
                roleRepository.save(adminRole);
            }

            if (appUserRepository.findByUsername("admin@universidad.edu").isEmpty()) {
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseThrow(() -> new IllegalStateException("No existe el rol ADMIN"));

                AppUser admin = new AppUser();
                admin.setUsername("admin@universidad.edu");
                admin.setPassword(passwordEncoder.encode("Admin1234!"));
                admin.setFullName("Administrador del Sistema");
                admin.setEmail("admin@universidad.edu");
                admin.getRoles().add(adminRole);
                appUserRepository.save(admin);
            }

            if (appUserRepository.findByUsername("testuser").isEmpty()) {
                Role userRole = roleRepository.findByName(RoleName.USER)
                        .orElseThrow(() -> new IllegalStateException("No existe el rol USER"));

                AppUser test = new AppUser();
                test.setUsername("testuser");
                test.setPassword(passwordEncoder.encode("Test1234!"));
                test.setFullName("Usuario de Prueba");
                test.setEmail("test@example.com");
                test.getRoles().add(userRole);
                appUserRepository.save(test);
            }
        };
    }
}
