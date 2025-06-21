package com.belajar.springboot.dto.response;

import com.belajar.springboot.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private UUID id;
    private CustomerResponse customer;

    private String createdBy;
    private BigDecimal netAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime transactionTime;
    private PaymentStatus paymentStatus;
    private String paymentMethod;

    private List<TransactionDetailResponse> details;

}
