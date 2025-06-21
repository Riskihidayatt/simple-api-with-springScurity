// src/main/java/com/enigma/live_code_springboot/repository/TransactionRepository.java
package com.belajar.springboot.repository;

import com.belajar.springboot.constant.PaymentMethod;
import com.belajar.springboot.constant.PaymentStatus;
import com.belajar.springboot.dto.response.ProductReportResponse;
import com.belajar.springboot.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    // Method filter yang sudah ada...
    Page<Transaction> findByCustomer_NameContainingIgnoreCase(String customerName,Pageable pageable);
    Page<Transaction> findByTransactionTimeBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable); // Tipe data LocalDateTime
    Page<Transaction> findByPaymentStatusIn(List<PaymentStatus> statuses, Pageable pageable);
    Page<Transaction> findByCreatedBy_Username(String username, Pageable pageable);
    Page<Transaction> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable); // Gunakan Enum


    // --- Query untuk Report Service ---
    @Query("SELECT SUM(t.totalAmount) FROM Transaction t " +
            "WHERE t.customer.id = :customerId " +
            "AND t.transactionTime BETWEEN :startDate AND :endDate " +
            "AND t.paymentStatus = com.enigma.live_code_springboot.constant.PaymentStatus.PAID")
    BigDecimal sumTotalAmountByCustomerIdAndTransactionTimeBetween(
            @Param("customerId") UUID customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t " +
            "WHERE t.customer.id = :customerId " +
            "AND t.paymentStatus = com.enigma.live_code_springboot.constant.PaymentStatus.PAID")
    BigDecimal sumTotalAmountByCustomerId(@Param("customerId") UUID customerId);

    // Query untuk laporan produk (menggunakan DTO Constructor Expression)
    @Query("SELECT new com.enigma.live_code_springboot.dto.response.ProductReportResponse(td.product.id, td.product.name, SUM(td.totalAmount)) " +
            "FROM TransactionDetail td JOIN td.transaction t " + // Join ke Transaction
            "WHERE t.paymentStatus = com.enigma.live_code_springboot.constant.PaymentStatus.PAID " + // Filter status PAID
            "GROUP BY td.product.id, td.product.name " + // Grouping
            "ORDER BY SUM(td.totalAmount) DESC") // Urutkan
    List<ProductReportResponse> findProductSpendingReport(); // <-- Nama method ini dipanggil di service

    // Untuk validasi delete customer
    boolean existsByCustomerId(UUID customerId);

    // Opsional: Untuk validasi delete product
    // boolean existsByDetails_ProductId(UUID productId); // Ini mungkin perlu query lebih spesifik
}