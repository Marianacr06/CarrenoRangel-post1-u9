package com.carrenorangel.authdemo.repository;

import com.carrenorangel.authdemo.entity.Role;
import com.carrenorangel.authdemo.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
