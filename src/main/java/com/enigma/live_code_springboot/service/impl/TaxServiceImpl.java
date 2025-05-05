// src/main/java/com/enigma/live_code_springboot/service/impl/TaxServiceImpl.java
package com.enigma.live_code_springboot.service.impl;

import com.enigma.live_code_springboot.dto.request.TaxRequest; // --> BUAT DTO INI <--
import com.enigma.live_code_springboot.dto.response.TaxResponse;
import com.enigma.live_code_springboot.entity.Tax;
import com.enigma.live_code_springboot.exception.BadRequestException;
import com.enigma.live_code_springboot.exception.ResourceNotFoundException;
import com.enigma.live_code_springboot.mapper.TaxMapper;
import com.enigma.live_code_springboot.repository.TaxRepository;
// import com.enigma.live_code_springboot.repository.ProductRepository; // Opsional
import com.enigma.live_code_springboot.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {

    private final TaxRepository taxRepository;

    @Override
    @Transactional(readOnly = true)
    public TaxResponse getById(UUID id) {
        Tax tax = findEntityById(id);
        return TaxMapper.toTaxResponse(tax);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxResponse> getAll() {
        return taxRepository.findAll().stream()
                .map(TaxMapper::toTaxResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(UUID id) {
        Tax tax = findEntityById(id);
        // TODO: Cek keterkaitan dengan Product sebelum delete
        taxRepository.delete(tax);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Tax> findTaxesByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        List<Tax> foundTaxes = taxRepository.findAllById(ids);
        return new HashSet<>(foundTaxes);
    }

    private Tax findEntityById(UUID id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax with ID " + id + " not found."));
    }
}