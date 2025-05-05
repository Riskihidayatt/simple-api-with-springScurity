// src/main/java/com/enigma/live_code_springboot/service/impl/ReportServiceImpl.java
package com.enigma.live_code_springboot.service.impl;

import com.enigma.live_code_springboot.constant.RoleType;
import com.enigma.live_code_springboot.dto.response.ReportResponse;// --> BUAT DTO INI <--
import com.enigma.live_code_springboot.entity.Customer;
import com.enigma.live_code_springboot.entity.User;
import com.enigma.live_code_springboot.exception.BadRequestException;
import com.enigma.live_code_springboot.exception.ResourceNotFoundException;
import com.enigma.live_code_springboot.exception.UnauthorizedException;
// import com.enigma.live_code_springboot.repository.CustomerRepository; // Optional
import com.enigma.live_code_springboot.repository.TransactionRepository;
import com.enigma.live_code_springboot.service.ReportService;
import com.enigma.live_code_springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import com.enigma.live_code_springboot.entity.Customer; // Import jika perlu ambil nama
import com.enigma.live_code_springboot.repository.CustomerRepository; // Import jika perlu
import com.enigma.live_code_springboot.util.PdfReportGenerator; // Import generator PDF
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CustomerRepository customerRepository;

    // private final CustomerRepository customerRepository; // Optional
    @Override
    @Transactional(readOnly = true)
    public byte[] generateCustomerSpendingPdf(UUID customerId, LocalDate startDate, LocalDate endDate) throws IOException {
        User currentUser = userService.getCurrentUser();
        UUID targetCustomerId = determineTargetCustomerId(currentUser, customerId, "generate customer spending report PDF");

        if (startDate == null || endDate == null) throw new BadRequestException("Start and end date required.");
        if (startDate.isAfter(endDate)) throw new BadRequestException("Start date cannot be after end date.");

        // 1. Ambil data total amount (gunakan metode yang sudah ada)
        BigDecimal total = getTotalAmountByCustomerAndDateRange(targetCustomerId, startDate, endDate);

        // 2. Ambil nama customer (opsional, untuk judul report)
        String customerName = customerRepository.findById(targetCustomerId)
                .map(Customer::getName)
                .orElse("Unknown Customer");

        // 3. Generate PDF menggunakan helper
        return PdfReportGenerator.generateCustomerSpendingReport(customerName, startDate, endDate, total);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateCustomerSpendingAllTimePdf(UUID customerId) throws IOException {
        User currentUser = userService.getCurrentUser();
        UUID targetCustomerId = determineTargetCustomerId(currentUser, customerId, "generate customer spending report PDF");

        // 1. Ambil data total amount
        BigDecimal total = getTotalAmountByCustomerAllTime(targetCustomerId);

        // 2. Ambil nama customer
        String customerName = customerRepository.findById(targetCustomerId)
                .map(Customer::getName)
                .orElse("Unknown Customer");

        // 3. Generate PDF
        return PdfReportGenerator.generateCustomerSpendingReport(customerName, null, null, total); // Pass null untuk dates
    }


    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByCustomerAndDateRange(UUID customerId, LocalDate startDate, LocalDate endDate) {
        User currentUser = userService.getCurrentUser();
        UUID targetCustomerId = determineTargetCustomerId(currentUser, customerId, "view customer spending report");

        if (startDate == null || endDate == null) throw new BadRequestException("Start and end date required.");
        if (startDate.isAfter(endDate)) throw new BadRequestException("Start date cannot be after end date.");

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // --- TODO: Buat method ini di TransactionRepository ---
        // BigDecimal total = transactionRepository.sumTotalAmountByCustomerIdAndTransactionTimeBetween(targetCustomerId, startDateTime, endDateTime);
        // return total != null ? total : BigDecimal.ZERO;
        // --- ----------------------------------------------- ---
        System.err.println("WARNING: Report query 'sumTotalAmountByCustomerIdAndTransactionTimeBetween' not implemented in Repository.");
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByCustomerAllTime(UUID customerId) {
        User currentUser = userService.getCurrentUser();
        UUID targetCustomerId = determineTargetCustomerId(currentUser, customerId, "view customer spending report");

        // --- TODO: Buat method ini di TransactionRepository ---
        // BigDecimal total = transactionRepository.sumTotalAmountByCustomerId(targetCustomerId);
        // return total != null ? total : BigDecimal.ZERO;
        // --- ----------------------------------------------- ---
        System.err.println("WARNING: Report query 'sumTotalAmountByCustomerId' not implemented in Repository.");
        return BigDecimal.ZERO;
    }

    // Implementasi setelah ProductSpendingReportResponse dibuat
    /*
    @Override
    @Transactional(readOnly = true)
    public List<ProductSpendingReportResponse> getTotalAmountPerProductAllTime() {
        User currentUser = userService.getCurrentUser();
        boolean isStaffOrAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleType.STAFF || r.getName() == RoleType.ADMIN);
        if (!isStaffOrAdmin) {
            throw new UnauthorizedException("Only Staff or Admin can access this report.");
        }

        // --- TODO: Buat method ini di TransactionRepository ---
        // return transactionRepository.findProductSpendingReport();
        // --- ----------------------------------------------- ---
        System.err.println("WARNING: Report query 'findProductSpendingReport' not implemented in Repository.");
        return List.of();
    }
    */

    private UUID determineTargetCustomerId(User currentUser, UUID requestedCustomerId, String action) {
        boolean isCustomer = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleType.CUSTOMER);
        boolean isStaffOrAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleType.STAFF || r.getName() == RoleType.ADMIN);

        if (isCustomer) {
            UUID customerId = getCustomerIdForUser(currentUser);
            if (customerId == null) throw new UnauthorizedException("Customer details not found for user.");
            if (requestedCustomerId != null && !requestedCustomerId.equals(customerId)) {
                throw new UnauthorizedException("Customers can only " + action + " for themselves.");
            }
            return customerId;
        } else if (isStaffOrAdmin) {
            if (requestedCustomerId == null) throw new BadRequestException("Customer ID is required for Staff/Admin to " + action + ".");
            // Opsional: cek customer valid
            // customerService.findEntityById(requestedCustomerId);
            return requestedCustomerId;
        } else {
            throw new UnauthorizedException("User role not permitted to " + action + ".");
        }
    }

    private UUID getCustomerIdForUser(User user) {
        // --- TODO: Implementasikan Logika Ini ---
        System.err.println("WARNING: Logic getCustomerIdForUser needs implementation!");
        return null;
    }
}