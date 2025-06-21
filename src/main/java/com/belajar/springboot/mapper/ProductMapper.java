package com.belajar.springboot.mapper;

import com.belajar.springboot.dto.response.ProductResponse;
import com.belajar.springboot.entity.Product;

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
