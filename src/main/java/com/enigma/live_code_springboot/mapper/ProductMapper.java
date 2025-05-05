package com.enigma.live_code_springboot.mapper;

import com.enigma.live_code_springboot.dto.response.ProductResponse;
import com.enigma.live_code_springboot.entity.Product;
import com.enigma.live_code_springboot.entity.Tax;

import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponse toProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .taxes(product.getTaxes().stream()
                        .map(TaxMapper::toTaxResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
