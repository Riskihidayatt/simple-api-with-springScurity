package com.enigma.live_code_springboot.dto.request;

import com.enigma.live_code_springboot.constant.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {

    @NotNull
    private String startDate;
    @NotNull
    private String endDate;

    private String customerName;

    private List<PaymentMethod> paymentMethods;

    private UUID createdBy;

    private Boolean sortByNewst;
}
