package com.enigma.live_code_springboot.controller;

import com.enigma.live_code_springboot.constant.ResponseMessage;
import com.enigma.live_code_springboot.dto.request.TaxRequest; // --> BUTUH DTO INI <--
import com.enigma.live_code_springboot.dto.response.CommonResponse;
import com.enigma.live_code_springboot.dto.response.TaxResponse;
import com.enigma.live_code_springboot.service.TaxService;
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
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Asumsi hanya Admin kelola pajak
public class TaxController {

    private final TaxService taxService;

    // Aktifkan setelah TaxRequest dibuat
    /*
    @PostMapping
    public ResponseEntity<CommonResponse<TaxResponse>> createTax(@Valid @RequestBody TaxRequest request) {
        TaxResponse taxResponse = taxService.create(request);
        return ResponseUtil.build(HttpStatus.CREATED, "Tax created successfully", taxResponse);
    }
    */

    // Aktifkan setelah TaxRequest dibuat
    /*
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<TaxResponse>> updateTax(@PathVariable UUID id, @Valid @RequestBody TaxRequest request) {
        TaxResponse taxResponse = taxService.update(id, request);
        return ResponseUtil.build(HttpStatus.OK, "Tax updated successfully", taxResponse);
    }
    */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')") // Staff mungkin perlu lihat pajak
    public ResponseEntity<CommonResponse<TaxResponse>> getTaxById(@PathVariable UUID id) {
        TaxResponse taxResponse = taxService.getById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), taxResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')") // Staff mungkin perlu lihat pajak
    public ResponseEntity<CommonResponse<List<TaxResponse>>> getAllTaxes() {
        List<TaxResponse> taxes = taxService.getAll();
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), taxes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<String>> deleteTax(@PathVariable UUID id) {
        taxService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK, "Tax deleted successfully", null);
    }
}