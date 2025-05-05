package com.enigma.live_code_springboot.dto.response;
import lombok.AllArgsConstructor; // Pastikan ada
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor // <-- PENTING untuk constructor expression di JPQL
@NoArgsConstructor
@Builder
public class ProductReportResponse {
    private UUID productId;       // Cocok dengan td.product.id (tipe UUID)
    private String productName;     // Cocok dengan td.product.name (tipe String)
    private BigDecimal totalAmountSpent; // Cocok dengan SUM(td.totalAmount) (tipe BigDecimal)
}