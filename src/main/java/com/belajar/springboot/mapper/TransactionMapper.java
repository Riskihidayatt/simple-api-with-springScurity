package com.belajar.springboot.mapper;

import com.belajar.springboot.dto.response.TransactionResponse;
import com.belajar.springboot.entity.Transaction;

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
