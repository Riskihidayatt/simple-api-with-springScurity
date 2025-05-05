// src/main/java/com/enigma/live_code_springboot/dto/request/TaxRequest.java
package com.enigma.live_code_springboot.dto.request;

import jakarta.validation.constraints.DecimalMin; // Untuk validasi minimal angka desimal
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxRequest {

    @NotBlank(message = "Tax name cannot be blank")
    private String name;

    @NotNull(message = "Tax percentage cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax percentage cannot be negative")
    private BigDecimal percentage;

}