package com.enigma.live_code_springboot.service;

import com.enigma.live_code_springboot.dto.request.ReportRequest;
import com.enigma.live_code_springboot.dto.response.ReportResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    BigDecimal getTotalAmountByCustomerAndDateRange(UUID customerId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalAmountByCustomerAllTime(UUID customerId);

    // Metode baru untuk PDF
    byte[] generateCustomerSpendingPdf(UUID customerId, LocalDate startDate, LocalDate endDate) throws IOException;
    byte[] generateCustomerSpendingAllTimePdf(UUID customerId) throws IOException;
}
