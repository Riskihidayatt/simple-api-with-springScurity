package com.belajar.springboot.dto.request;

import com.belajar.springboot.constant.PaymentMethod;
import com.belajar.springboot.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionFilterRequest {
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PaymentStatus>paymentStatus;
    private List<PaymentMethod>paymentMethods;
    private UUID staffId;
    private Boolean sortByNewst;

}
