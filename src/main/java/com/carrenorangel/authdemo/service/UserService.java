package com.carrenorangel.authdemo.service;

import com.carrenorangel.authdemo.entity.AppUser;
import com.carrenorangel.authdemo.entity.Role;
import com.carrenorangel.authdemo.entity.RoleName;
import com.carrenorangel.authdemo.repository.AppUserRepository;
import com.carrenorangel.authdemo.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser registerUser(String username, String password, String fullName, String email) {
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        if (appUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalStateException("No existe el rol USER"));

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setFullName(fullName);
        appUser.setEmail(email);
        Set<Role> roles = new HashSet<>(Collections.singletonList(userRole));
        appUser.setRoles(roles);
        return appUserRepository.save(appUser);
    }
}
