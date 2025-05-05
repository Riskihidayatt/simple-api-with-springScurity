// src/main/java/com/enigma/live_code_springboot/entity/Product.java
package com.enigma.live_code_springboot.entity;

import com.enigma.live_code_springboot.constant.TableNames;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = TableNames.PRODUCT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToMany(fetch = FetchType.EAGER) // Eager mungkin OK jika pajak selalu dibutuhkan
    @JoinTable(
            name = TableNames.PRODUCT_TAXES, // <-- Gunakan konstanta
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tax_id")
    )
    @Builder.Default
    private Set<Tax> taxes = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}