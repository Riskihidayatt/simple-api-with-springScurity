package com.belajar.springboot.mapper;

import com.belajar.springboot.dto.response.TransactionDetailResponse;
import com.belajar.springboot.entity.TransactionDetail;

public class TransactionDetailMapper {
        public static TransactionDetailResponse toDetailResponse(TransactionDetail detail){
            return TransactionDetailResponse.builder()
                    .id(detail.getId())
                    .product(ProductMapper.toProductResponse(detail.getProduct()))
                    .quantity(detail.getQuantity())
                    .price(detail.getPriceAtTransaction())
                    .totalAmount(detail.getTotalAmount())
                    .build();
        }
}
