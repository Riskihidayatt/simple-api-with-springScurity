// src/main/java/com/enigma/live_code_springboot/config/BeanConfiguration.java
package com.enigma.live_code_springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Import JavaTimeModule
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Tambahkan Bean ObjectMapper di sini agar bisa diinject
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register module untuk handle Java 8 Date/Time API (LocalDate, LocalDateTime)
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
