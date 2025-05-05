// src/main/java/com/enigma/live_code_springboot/controller/ReportController.java
package com.enigma.live_code_springboot.controller;

import com.enigma.live_code_springboot.constant.ResponseMessage;
// import com.enigma.live_code_springboot.dto.response.ProductReportResponse; // Masih dibutuhkan jika endpoint produk diaktifkan
import com.enigma.live_code_springboot.dto.response.CommonResponse;
import com.enigma.live_code_springboot.service.ReportService;
import com.enigma.live_code_springboot.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal; // Import BigDecimal karena service mengembalikan ini
import java.time.LocalDate;
import java.util.List; // Import List (untuk endpoint produk nanti)
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // Semua endpoint report butuh login
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/customer/spending/date-range")
    // Ubah T di CommonResponse menjadi BigDecimal
    public ResponseEntity<CommonResponse<BigDecimal>> getCustomerSpendingByDate(
            @RequestParam(required = false) UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Panggil service yang mengembalikan BigDecimal
        BigDecimal total = reportService.getTotalAmountByCustomerAndDateRange(customerId, startDate, endDate);
        // Gunakan ResponseUtil untuk membungkus BigDecimal
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.TRANSACTION_REPORT_SUCCESS.getMessage(), total);
    }


    @GetMapping("/customer/spending/all-time")
    // Ubah T di CommonResponse menjadi BigDecimal
    public ResponseEntity<CommonResponse<BigDecimal>> getCustomerSpendingAllTime(
            @RequestParam(required = false) UUID customerId
    ) {
        // Panggil service yang mengembalikan BigDecimal
        BigDecimal total = reportService.getTotalAmountByCustomerAllTime(customerId);
        // Gunakan ResponseUtil untuk membungkus BigDecimal
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.TRANSACTION_REPORT_SUCCESS.getMessage(), total);
    }

    // Laporan Product - All Time (Staff/Admin Only)
    // Endpoint ini masih dikomentari karena service method dan DTO-nya juga placeholder/belum tentu ada
    /*
    @GetMapping("/product/spending/all-time")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    // Gunakan DTO ProductReportResponse yang benar jika diaktifkan
    public ResponseEntity<CommonResponse<List<ProductReportResponse>>> getProductSpendingAllTime() {
        List<ProductReportResponse> report = reportService.getTotalAmountPerProductAllTime();
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.TRANSACTION_REPORT_SUCCESS.getMessage(), report);
    }
    */

    // Endpoint PDF untuk Date Range
    @GetMapping("/customer/spending/date-range/pdf")
    public ResponseEntity<byte[]> downloadCustomerSpendingByDatePdf(
            @RequestParam(required = false) UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            byte[] pdfBytes = reportService.generateCustomerSpendingPdf(customerId, startDate, endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Nama file bisa dibuat lebih dinamis
            String filename = "laporan_pengeluaran_" + (customerId != null ? customerId.toString() : "semua") + "_" + startDate + "_sd_" + endDate + ".pdf";
            headers.setContentDispositionFormData("attachment", filename); // Minta browser download
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            // Handle error pembuatan PDF - mungkin return error 500
            // Anda bisa menggunakan GlobalExceptionHandler atau return manual
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            // Tangani exception lain dari service (misal: BadRequest, Unauthorized)
            // Sebaiknya biarkan GlobalExceptionHandler yang menangani ini
            throw e;
        }
    }

    // Endpoint PDF untuk All Time
    @GetMapping("/customer/spending/all-time/pdf")
    public ResponseEntity<byte[]> downloadCustomerSpendingAllTimePdf(
            @RequestParam(required = false) UUID customerId
    ) {
        try {
            byte[] pdfBytes = reportService.generateCustomerSpendingAllTimePdf(customerId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "laporan_pengeluaran_" + (customerId != null ? customerId.toString() : "semua") + "_all_time.pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            throw e;
        }
    }

    // Endpoint Laporan Product PDF (jika diaktifkan)
    /*
    @GetMapping("/product/spending/all-time/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<byte[]> downloadProductSpendingAllTimePdf() {
       // ... Logika mirip: panggil service, generate PDF, set headers ...
    }
    */
}