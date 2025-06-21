package com.belajar.springboot.repository;

import com.belajar.springboot.constant.RoleType;
import com.belajar.springboot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleType name);
}
