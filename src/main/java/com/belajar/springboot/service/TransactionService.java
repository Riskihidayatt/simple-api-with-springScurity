//package com.enigma.live_code_springboot.service;
//
//import com.enigma.live_code_springboot.constant.PaymentStatus;
//import com.enigma.live_code_springboot.dto.request.TransactionFilterRequest;
//import com.enigma.live_code_springboot.dto.request.TransactionRequest;
//import com.enigma.live_code_springboot.dto.response.TransactionResponse;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.UUID;
//
//public interface TransactionService {
//    TransactionResponse create(TransactionRequest request);
//    TransactionResponse getById(UUID id);
//    Page<TransactionResponse> getAllFiltered(TransactionFilterRequest request, Pageable pageable);
//    TransactionResponse updateStatus(UUID id, PaymentStatus status);
//
//}

// src/main/java/com/enigma/live_code_springboot/service/TransactionService.java
package com.belajar.springboot.service;

import com.belajar.springboot.constant.PaymentStatus;
import com.belajar.springboot.dto.request.TransactionFilterRequest;
import com.belajar.springboot.dto.request.TransactionRequest;
import com.belajar.springboot.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface untuk layanan pengelolaan Transaksi.
 */
public interface TransactionService {

    TransactionResponse create(TransactionRequest request);

    TransactionResponse getById(UUID id);

    Page<TransactionResponse> getAllFiltered(TransactionFilterRequest request, Pageable pageable);

    TransactionResponse updateStatus(UUID id, PaymentStatus status);
}
