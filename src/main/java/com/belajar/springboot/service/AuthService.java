// src/main/java/com/enigma/live_code_springboot/service/AuthService.java
package com.belajar.springboot.service;

import com.belajar.springboot.dto.request.LoginRequest;
import com.belajar.springboot.dto.request.UserRegisterRequest; // Menggunakan DTO ini
import com.belajar.springboot.dto.response.JwtResponse;
import com.belajar.springboot.dto.response.UserResponse;


public interface AuthService {

    UserResponse registerCustomer(UserRegisterRequest request);

    UserResponse registerStaff(UserRegisterRequest request);

    JwtResponse login(LoginRequest request);
}