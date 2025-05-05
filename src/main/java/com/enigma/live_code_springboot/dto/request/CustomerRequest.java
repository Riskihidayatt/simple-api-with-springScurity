// src/main/java/com/enigma/live_code_springboot/dto/request/CustomerRequest.java
package com.enigma.live_code_springboot.dto.request;

import jakarta.validation.constraints.Email; // Tambahkan import Email
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size; // Tambahkan import Size
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "Customer name cannot be blank")
    private String name;

    @NotNull(message = "Birthdate cannot be null")
    private String birthdate;

    @NotBlank(message = "Birthplace cannot be blank")
    private String birthplace;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Email(message = "Invalid email format for login")
    private String email;

    @Size(min = 8, message = "Login password must be at least 8 characters")
    private String password;

}