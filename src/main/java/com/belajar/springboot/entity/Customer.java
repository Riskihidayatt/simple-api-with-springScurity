// src/main/java/com/enigma/live_code_springboot/entity/Customer.java
package com.belajar.springboot.entity;

import com.belajar.springboot.constant.TableNames;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = TableNames.CUSTOMER) // Gunakan konstanta yang benar
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private LocalDate birthDate;
    private String birthPlace;

    // Relasi ke User (untuk data Customer yang terkait login)
    @Column(name = "user_id", unique = true) // Harus unik jika 1 user = 1 customer
    private UUID userId; // <-- Tambahkan ini

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch lebih efisien di sini
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "updated_at") // Ganti nama kolom
    private LocalDateTime updatedAt; // <-- Ganti nama field

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now(); // Set updatedAt saat update
    }
}