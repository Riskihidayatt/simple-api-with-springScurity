// src/main/java/com/enigma/live_code_springboot/entity/Transaction.java
package com.enigma.live_code_springboot.entity;

import com.enigma.live_code_springboot.constant.PaymentMethod;
import com.enigma.live_code_springboot.constant.PaymentStatus;
import com.enigma.live_code_springboot.constant.TableNames;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = TableNames.TRANSACTIONS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch lebih efisien
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false) // <-- Perbaiki typo kolom
    private User createdBy; // Nama field sudah benar

    @Column(nullable = false)
    private BigDecimal netAmount;

    @Column(nullable = false)
    private BigDecimal taxAmount;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    // --- TAMBAHKAN FIELD INI ---
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    // --- AKHIR PENAMBAHAN ---

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<TransactionDetail> details = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (transactionTime == null) transactionTime = LocalDateTime.now();
        if (paymentStatus == null) paymentStatus = PaymentStatus.NOT_PAID;
        // Set default amount jika null
        if (netAmount == null) netAmount = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        // Payment method harus diisi dari request
    }
}