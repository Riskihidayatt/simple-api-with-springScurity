package com.belajar.springboot.dto.request;

import com.belajar.springboot.constant.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class TransactionRequest {

    @NotNull
    private UUID customerId;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotEmpty
    private List<@Valid TransactionDetailRequest> details;


}
