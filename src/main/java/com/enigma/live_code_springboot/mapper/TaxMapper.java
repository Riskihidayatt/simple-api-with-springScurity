package com.enigma.live_code_springboot.mapper;

import com.enigma.live_code_springboot.dto.response.TaxResponse;
import com.enigma.live_code_springboot.entity.Tax;

public class TaxMapper {
    public static TaxResponse toTaxResponse(Tax tax){
        return TaxResponse.builder()
                .id(tax.getId())
                .name(tax.getName())
                .percentage(tax.getPercentage())
                .build();
    }
}
