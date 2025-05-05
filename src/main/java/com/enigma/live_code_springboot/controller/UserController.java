// src/main/java/com/enigma/live_code_springboot/controller/UserController.java
package com.enigma.live_code_springboot.controller;

import com.enigma.live_code_springboot.constant.ResponseMessage;
import com.enigma.live_code_springboot.dto.request.UpdateProfileRequest;
import com.enigma.live_code_springboot.dto.response.CommonResponse;
import com.enigma.live_code_springboot.dto.response.UserResponse;
import com.enigma.live_code_springboot.service.UserService;
import com.enigma.live_code_springboot.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Update profil user sendiri (STAFF/CUSTOMER/ADMIN)
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority()")
    public ResponseEntity<CommonResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse userResponse = userService.updateProfile(request);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.USER_UPDATED.getMessage(), userResponse);
    }

    // --- Endpoint khusus ADMIN ---

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse userResponse = userService.getById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), userResponse);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAll();
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), userResponses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.USER_DELETED.getMessage(), null);
    }

}