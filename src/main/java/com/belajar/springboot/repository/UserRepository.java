package com.belajar.springboot.repository;

import com.belajar.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsernameOrEmail(String username, String email);
   boolean existsByUsername(String username);
   boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    // --- AKHIR PENAMBAHAN ---

    // Method ini mungkin juga perlu jika belum ada
    Optional<User> findByEmail(String email);
}
