
package com.enigma.live_code_springboot.controller;

import com.enigma.live_code_springboot.constant.PaymentMethod; // Import PaymentMethod
import com.enigma.live_code_springboot.constant.PaymentStatus;
import com.enigma.live_code_springboot.constant.ResponseMessage;
import com.enigma.live_code_springboot.dto.request.TransactionFilterRequest;
import com.enigma.live_code_springboot.dto.request.TransactionRequest;
import com.enigma.live_code_springboot.dto.response.CommonResponse;
import com.enigma.live_code_springboot.dto.response.TransactionResponse;
import com.enigma.live_code_springboot.service.TransactionService;
import com.enigma.live_code_springboot.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat; // Untuk parsing tanggal/waktu
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Import LocalDateTime
import java.time.format.DateTimeParseException; // Untuk error parsing
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse<TransactionResponse>> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.create(request);
        return ResponseUtil.build(HttpStatus.CREATED, ResponseMessage.TRANSACTION_CREATED.getMessage(), transactionResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Otorisasi detail di service
    public ResponseEntity<CommonResponse<TransactionResponse>> getTransactionById(@PathVariable UUID id) {
        TransactionResponse transactionResponse = transactionService.getById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), transactionResponse);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // Otorisasi detail di service
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> getFilteredTransactions(
            // --- Parameter Filter (Opsional) ---
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, // Terima LocalDateTime
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<PaymentStatus> paymentStatus,
            // @RequestParam(required = false) List<PaymentMethod> paymentMethods, // Tidak bisa difilter karena tidak ada di entity
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) Boolean sortByNewest, // Sesuai DTO Anda
            // --- Pagination ---
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        // Buat DTO Filter dari parameter
        TransactionFilterRequest filterRequest = TransactionFilterRequest.builder()
                .customerName(customerName)
                .startDate(startDate)
                .endDate(endDate)
                .paymentStatus(paymentStatus)
                // .paymentMethods(paymentMethods) // Tidak bisa difilter
                .staffId(staffId)
                .sortByNewst(sortByNewest) // Sesuai DTO
                .build();

        Page<TransactionResponse> transactionPage = transactionService.getAllFiltered(filterRequest, pageable);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), transactionPage.getContent(), transactionPage);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse<TransactionResponse>> updateTransactionStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status // Ambil status dari query param
    ) {
        TransactionResponse transactionResponse = transactionService.updateStatus(id, status);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.TRANSACTION_UPDATED.getMessage(), transactionResponse);
    }
}