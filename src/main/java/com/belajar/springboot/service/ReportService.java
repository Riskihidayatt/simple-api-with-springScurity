package com.belajar.springboot.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
    BigDecimal getTotalAmountByCustomerAndDateRange(UUID customerId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalAmountByCustomerAllTime(UUID customerId);

    // Metode baru untuk PDF
    byte[] generateCustomerSpendingPdf(UUID customerId, LocalDate startDate, LocalDate endDate) throws IOException;
    byte[] generateCustomerSpendingAllTimePdf(UUID customerId) throws IOException;
}
