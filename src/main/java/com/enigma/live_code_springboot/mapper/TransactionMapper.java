package com.enigma.live_code_springboot.mapper;

import com.enigma.live_code_springboot.dto.response.TransactionResponse;
import com.enigma.live_code_springboot.entity.Transaction;

import java.util.stream.Collectors;

public class TransactionMapper {

    public static TransactionResponse toTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .customer(CustomerMapper.toCustomerResponse(transaction.getCustomer()))
                .netAmount(transaction.getNetAmount())
                .totalAmount(transaction.getTotalAmount())
                .transactionTime(transaction.getTransactionTime())
                .paymentStatus(transaction.getPaymentStatus())
                .createdBy(transaction.getCreatedBy().toString())
                .details(transaction.getDetails().stream().map(TransactionDetailMapper::toDetailResponse).collect(Collectors.toList()))

        .build();
    }
}
