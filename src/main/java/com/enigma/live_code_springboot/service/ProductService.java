// src/main/java/com/enigma/live_code_springboot/service/ProductService.java
package com.enigma.live_code_springboot.service;

import com.enigma.live_code_springboot.dto.request.ProductRequest;
import com.enigma.live_code_springboot.dto.response.ProductResponse;
import com.enigma.live_code_springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface untuk layanan pengelolaan Product.
 */
public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(UUID id, ProductRequest request);

    ProductResponse getById(UUID id);

    Page<ProductResponse> getAll(Pageable pageable);

    void deleteById(UUID id);

    Product findEntityById(UUID id);
}
