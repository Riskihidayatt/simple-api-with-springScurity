// src/main/java/com/enigma/live_code_springboot/service/AuthService.java
package com.enigma.live_code_springboot.service;

import com.enigma.live_code_springboot.dto.request.LoginRequest;
import com.enigma.live_code_springboot.dto.request.UserRegisterRequest; // Menggunakan DTO ini
import com.enigma.live_code_springboot.dto.response.JwtResponse;
import com.enigma.live_code_springboot.dto.response.UserResponse;


public interface AuthService {

    UserResponse registerCustomer(UserRegisterRequest request);

    UserResponse registerStaff(UserRegisterRequest request);

    JwtResponse login(LoginRequest request);
}