
package com.belajar.springboot.service.impl;

import com.belajar.springboot.dto.request.ProductRequest;
import com.belajar.springboot.dto.response.ProductResponse;
import com.belajar.springboot.entity.Product;
import com.belajar.springboot.entity.Tax;
import com.belajar.springboot.exception.ResourceNotFoundException;
import com.belajar.springboot.mapper.ProductMapper;
import com.belajar.springboot.repository.ProductRepository;
// import com.enigma.live_code_springboot.repository.TransactionDetailRepository; // Optional: Inject jika perlu cek relasi
import com.belajar.springboot.service.ProductService;
import com.belajar.springboot.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TaxService taxService;
    // private final TransactionDetailRepository transactionDetailRepository; // Opsional

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse create(ProductRequest request) {
        Set<Tax> taxes = taxService.findTaxesByIds(request.getTaxIds());

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .taxes(taxes)
                .build();

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toProductResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse update(UUID id, ProductRequest request) {
        Product existingProduct = findEntityById(id);
        Set<Tax> taxes = taxService.findTaxesByIds(request.getTaxIds());

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setTaxes(taxes);

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toProductResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        Product product = findEntityById(id);
        return ProductMapper.toProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(UUID id) {
        Product product = findEntityById(id);
        // TODO: Cek keterkaitan dengan TransactionDetail
        // if (transactionDetailRepository.existsByProductId(id)) { // Contoh jika methodnya ada
        //     throw new BusinessLogicException("Cannot delete product because it exists in transactions.");
        // }
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findEntityById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found."));
    }
}