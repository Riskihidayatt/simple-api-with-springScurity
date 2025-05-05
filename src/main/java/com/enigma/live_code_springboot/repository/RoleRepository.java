package com.enigma.live_code_springboot.repository;

import com.enigma.live_code_springboot.constant.RoleType;
import com.enigma.live_code_springboot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleType name);
}
