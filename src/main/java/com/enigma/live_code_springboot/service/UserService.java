// src/main/java/com/enigma/live_code_springboot/service/UserService.java
package com.enigma.live_code_springboot.service;

import com.enigma.live_code_springboot.dto.request.UpdateProfileRequest;
import com.enigma.live_code_springboot.dto.response.UserResponse;
import com.enigma.live_code_springboot.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService; // Penting

import java.util.List;
import java.util.UUID;

/**
 * Interface untuk layanan pengelolaan User.
 * Juga bertindak sebagai UserDetailsService untuk Spring Security.
 */
public interface UserService extends UserDetailsService { // Extends UserDetailsService

    User findEntityById(UUID id);

    UserResponse getById(UUID id);

    List<UserResponse> getAll();
    void deleteById(UUID id);

    UserResponse updateProfile(UpdateProfileRequest request);

    User getCurrentUser();
}