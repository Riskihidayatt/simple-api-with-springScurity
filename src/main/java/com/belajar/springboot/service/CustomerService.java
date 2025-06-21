// src/main/java/com/enigma/live_code_springboot/service/CustomerService.java
package com.belajar.springboot.service;

import com.belajar.springboot.dto.request.CustomerRequest;
import com.belajar.springboot.dto.response.CustomerResponse;
import com.belajar.springboot.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(UUID id, CustomerRequest request);

    CustomerResponse getById(UUID id);

    Page<CustomerResponse> getAll(Pageable pageable);

    void deleteById(UUID id);

    Customer findEntityById(UUID id);
}