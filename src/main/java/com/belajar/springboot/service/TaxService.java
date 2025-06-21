// src/main/java/com/enigma/live_code_springboot/service/TaxService.java
package com.belajar.springboot.service;

// import com.enigma.live_code_springboot.dto.request.TaxRequest; // Butuh DTO ini
import com.belajar.springboot.dto.response.TaxResponse;
import com.belajar.springboot.entity.Tax;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface TaxService {

    TaxResponse getById(UUID id);
    List<TaxResponse> getAll();
    void deleteById(UUID id);
    Set<Tax> findTaxesByIds(Set<UUID> ids);
}