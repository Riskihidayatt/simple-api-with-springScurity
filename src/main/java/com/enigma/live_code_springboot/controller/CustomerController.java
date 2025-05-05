package com.enigma.live_code_springboot.controller;

import com.enigma.live_code_springboot.constant.ResponseMessage;
import com.enigma.live_code_springboot.dto.request.CustomerRequest;
import com.enigma.live_code_springboot.dto.response.CommonResponse;
import com.enigma.live_code_springboot.dto.response.CustomerResponse;
import com.enigma.live_code_springboot.service.CustomerService;
import com.enigma.live_code_springboot.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
// Otorisasi default di level class untuk STAFF atau ADMIN
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CommonResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse customerResponse = customerService.create(request);
        return ResponseUtil.build(HttpStatus.CREATED, ResponseMessage.CUSTOMER_CREATED.getMessage(), customerResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        CustomerResponse customerResponse = customerService.update(id, request);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.CUSTOMER_UPDATED.getMessage(), customerResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id) {
        CustomerResponse customerResponse = customerService.getById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), customerResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CustomerResponse>>> getAllCustomers(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<CustomerResponse> customerPage = customerService.getAll(pageable);
        // Menggunakan ResponseUtil yang handle pagination
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), customerPage.getContent(), customerPage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.CUSTOMER_DELETED.getMessage(), null);
    }
}