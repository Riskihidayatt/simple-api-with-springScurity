// src/main/java/com/enigma/live_code_springboot/service/impl/TaxServiceImpl.java
package com.belajar.springboot.service.impl;

import com.belajar.springboot.dto.response.TaxResponse;
import com.belajar.springboot.entity.Tax;
import com.belajar.springboot.exception.ResourceNotFoundException;
import com.belajar.springboot.mapper.TaxMapper;
import com.belajar.springboot.repository.TaxRepository;
// import com.enigma.live_code_springboot.repository.ProductRepository; // Opsional
import com.belajar.springboot.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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