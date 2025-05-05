// src/main/java/com/enigma/live_code_springboot/specification/TransactionSpecification.java
package com.enigma.live_code_springboot.specification;

import com.enigma.live_code_springboot.dto.request.TransactionFilterRequest;
import com.enigma.live_code_springboot.entity.Transaction;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> getSpecification(TransactionFilterRequest request) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ... (Filter customerName, startDate, endDate, paymentStatus seperti sebelumnya) ...

            // Filter berdasarkan metode pembayaran (JIKA ADA DI REQUEST & FIELD ADA DI ENTITY)
            if (request.getPaymentMethods() != null && !request.getPaymentMethods().isEmpty()) {
                // Pastikan nama field di entity adalah 'paymentMethod'
                predicates.add(root.get("paymentMethod").in(request.getPaymentMethods())); // <-- Tambahkan ini
            }

            // ... (Filter staffId seperti sebelumnya) ...

            // Sorting TIDAK dihandle di sini lagi, karena akan dihandle oleh Pageable di Service
            // Hapus bagian sorting dari spesifikasi jika Anda menghandlenya di Service
            /*
            if (request.getSortByNewst() != null) {
                if (request.getSortByNewst()) {
                    query.orderBy(criteriaBuilder.desc(root.get("transactionTime")));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get("transactionTime")));
                }
            }
            */

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}