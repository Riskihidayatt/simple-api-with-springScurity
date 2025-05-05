package com.enigma.live_code_springboot.dto.request;

import com.enigma.live_code_springboot.constant.PaymentMethod;
import com.enigma.live_code_springboot.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
