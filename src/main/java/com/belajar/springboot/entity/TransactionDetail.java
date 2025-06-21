// src/main/java/com/enigma/live_code_springboot/entity/TransactionDetail.java
package com.belajar.springboot.entity;

import com.belajar.springboot.constant.TableNames;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = TableNames.TRANSACTION_DETAILS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetail {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch ok
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch ok
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_transaction", nullable = false) // <-- Ganti nama kolom & field
    private BigDecimal priceAtTransaction; // <-- Ganti nama field

    @Column(nullable = false)
    private BigDecimal totalAmount; // Total per baris (termasuk pajak)

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID(); // <-- Lengkapi ini
        }
    }
}