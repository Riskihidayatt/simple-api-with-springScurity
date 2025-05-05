// src/main/java/com/enigma/live_code_springboot/controller/AuthController.java
package com.enigma.live_code_springboot.controller;

import com.enigma.live_code_springboot.constant.ResponseMessage;
import com.enigma.live_code_springboot.dto.request.LoginRequest;
import com.enigma.live_code_springboot.dto.request.UserRegisterRequest;
import com.enigma.live_code_springboot.dto.response.CommonResponse;
import com.enigma.live_code_springboot.dto.response.JwtResponse;
import com.enigma.live_code_springboot.dto.response.UserResponse;
import com.enigma.live_code_springboot.service.AuthService;
import com.enigma.live_code_springboot.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-customer")
    public ResponseEntity<CommonResponse<UserResponse>> registerCustomer(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse userResponse = authService.registerCustomer(request);
        return ResponseUtil.build(HttpStatus.CREATED, ResponseMessage.REGISTER_SUCCESS.getMessage(), userResponse);
    }

    // Endpoint registrasi Staff (hanya oleh ADMIN)
    @PostMapping("/register-staff")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponse>> registerStaff(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse userResponse = authService.registerStaff(request);
        return ResponseUtil.build(HttpStatus.CREATED, ResponseMessage.USER_CREATED.getMessage(), userResponse);
    }

    // Endpoint Login
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse jwtResponse = authService.login(request);
        // Gunakan ResponseUtil untuk membuat respons
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.LOGIN_SUCCESS.getMessage(), jwtResponse);
    }
}