package com.enigma.live_code_springboot.mapper;

import com.enigma.live_code_springboot.dto.response.CustomerResponse;
import com.enigma.live_code_springboot.entity.Customer;
import com.enigma.live_code_springboot.entity.User; // <-- Import User
import java.time.format.DateTimeFormatter; // <-- Import Formatter

public class CustomerMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Format tanggal

    public static CustomerResponse toCustomerResponse(Customer customer) {
        // Jika objek customer-nya sendiri null, kembalikan null
        if (customer == null) {
            return null;
        }

        User createdByUser = customer.getCreatedBy(); // Ambil objek User nya dulu

        String createdByUsername = (createdByUser != null) ? createdByUser.getUsername() : null;


        User updatedByUser = customer.getUpdatedBy();
        String updatedByUsername = (updatedByUser != null) ? updatedByUser.getUsername() : null;

        String birthdateString = (customer.getBirthDate() != null)
                ? customer.getBirthDate().format(DATE_FORMATTER)
                : null;

        // 4. Bangun respons DTO menggunakan variabel yang sudah aman (bisa null)
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .birthdate(birthdateString)    // <-- Gunakan hasil format/null
                .birthplace(customer.getBirthPlace())
                .createdAt(customer.getCreatedAt())
                .createdBy(createdByUsername) // <-- Gunakan hasil cek null
                .updatedAt(customer.getUpdatedAt()) // Waktu update bisa null
                .updatedBy(updatedByUsername) // <-- Gunakan hasil cek null
                .build();
    }
}