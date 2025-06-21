package com.belajar.springboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailResponse {
    private UUID id;
    private ProductResponse product;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
}
