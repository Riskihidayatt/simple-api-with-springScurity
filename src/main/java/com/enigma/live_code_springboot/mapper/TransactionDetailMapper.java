package com.enigma.live_code_springboot.mapper;

import com.enigma.live_code_springboot.dto.response.TransactionDetailResponse;
import com.enigma.live_code_springboot.entity.TransactionDetail;

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
