// src/main/java/com/enigma/live_code_springboot/repository/CustomerRepository.java
package com.enigma.live_code_springboot.repository;

import com.enigma.live_code_springboot.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Method untuk menghubungkan User ke Customer
    Optional<Customer> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId); // Untuk cek saat registrasi user baru
}