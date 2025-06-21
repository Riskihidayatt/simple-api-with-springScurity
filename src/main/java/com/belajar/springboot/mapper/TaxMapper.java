package com.belajar.springboot.mapper;

import com.belajar.springboot.dto.response.TaxResponse;
import com.belajar.springboot.entity.Tax;

public class TaxMapper {
    public static TaxResponse toTaxResponse(Tax tax){
        return TaxResponse.builder()
                .id(tax.getId())
                .name(tax.getName())
                .percentage(tax.getPercentage())
                .build();
    }
}
