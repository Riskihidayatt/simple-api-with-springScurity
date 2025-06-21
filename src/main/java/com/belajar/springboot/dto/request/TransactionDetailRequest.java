package com.belajar.springboot.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private int quantity;
}
