package com.belajar.springboot.controller;

import com.belajar.springboot.constant.ResponseMessage;
import com.belajar.springboot.dto.request.ProductRequest;
import com.belajar.springboot.dto.response.CommonResponse;
import com.belajar.springboot.dto.response.ProductResponse;
import com.belajar.springboot.service.ProductService;
import com.belajar.springboot.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.create(request);
        return ResponseUtil.build(HttpStatus.CREATED, ResponseMessage.PRODUCT_CREATED.getMessage(), productResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.update(id, request);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.PRODUCT_UPDATED.getMessage(), productResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')") // Atau isAuthenticated() jika semua boleh lihat
    public ResponseEntity<CommonResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse productResponse = productService.getById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), productResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')") // Atau isAuthenticated() jika semua boleh lihat
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> productPage = productService.getAll(pageable);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.SUCCESS.getMessage(), productPage.getContent(), productPage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')") // Hanya Admin boleh delete
    public ResponseEntity<CommonResponse<String>> deleteProduct(@PathVariable UUID id) {
        productService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK, ResponseMessage.PRODUCT_DELETED.getMessage(), null);
    }
}