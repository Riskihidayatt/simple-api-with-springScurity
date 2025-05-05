package com.enigma.live_code_springboot.service.impl;

import com.enigma.live_code_springboot.constant.PaymentStatus;
import com.enigma.live_code_springboot.constant.RoleType;
import com.enigma.live_code_springboot.dto.request.TransactionDetailRequest;
import com.enigma.live_code_springboot.dto.request.TransactionFilterRequest;
import com.enigma.live_code_springboot.dto.request.TransactionRequest;
import com.enigma.live_code_springboot.dto.response.TransactionResponse;
import com.enigma.live_code_springboot.entity.*;
import com.enigma.live_code_springboot.exception.BadRequestException;
import com.enigma.live_code_springboot.exception.BusinessLogicException;
import com.enigma.live_code_springboot.exception.ResourceNotFoundException;
import com.enigma.live_code_springboot.exception.UnauthorizedException;
import com.enigma.live_code_springboot.mapper.TransactionMapper;
import com.enigma.live_code_springboot.repository.TransactionDetailRepository;
import com.enigma.live_code_springboot.repository.TransactionRepository;
import com.enigma.live_code_springboot.service.CustomerService;
import com.enigma.live_code_springboot.service.ProductService;
import com.enigma.live_code_springboot.service.TransactionService;
import com.enigma.live_code_springboot.service.UserService;
import com.enigma.live_code_springboot.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final UserService userService;
    // private final CustomerRepository customerRepository; // Inject jika perlu untuk getCustomerIdForUser

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse create(TransactionRequest request) {
        User currentStaffOrAdmin = userService.getCurrentUser();
        boolean canCreate = currentStaffOrAdmin.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.STAFF || role.getName() == RoleType.ADMIN);
        if (!canCreate) {
            throw new UnauthorizedException("Only STAFF or ADMIN can create transactions.");
        }

        Customer customer = customerService.findEntityById(request.getCustomerId());

        BigDecimal totalNetAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        Transaction transaction = Transaction.builder()
                .customer(customer)
                .createdBy(currentStaffOrAdmin) // Ingat typo `creted_by` di entity
                .transactionTime(LocalDateTime.now())
                .paymentStatus(PaymentStatus.NOT_PAID)
                .paymentMethod(request.getPaymentMethod()) // <-- FIELD INI HILANG DARI ENTITY ANDA
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        List<TransactionDetail> transactionDetails = new ArrayList<>();
        for (TransactionDetailRequest detailRequest : request.getDetails()) {
            Product product = productService.findEntityById(detailRequest.getProductId());
            int quantityInt = detailRequest.getQuantity();
            if (quantityInt <= 0) throw new BadRequestException("Quantity must be positive.");

            BigDecimal quantity = new BigDecimal(quantityInt);
            BigDecimal priceAtTransaction = product.getPrice(); // Harga saat itu

            BigDecimal lineNetAmount = priceAtTransaction.multiply(quantity);
            BigDecimal lineTaxAmount = BigDecimal.ZERO;

            if (product.getTaxes() != null) {
                for (Tax tax : product.getTaxes()) {
                    BigDecimal taxRate = tax.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    lineTaxAmount = lineTaxAmount.add(lineNetAmount.multiply(taxRate));
                }
            }
            BigDecimal lineTotalAmount = lineNetAmount.add(lineTaxAmount);

            totalNetAmount = totalNetAmount.add(lineNetAmount);
            totalTaxAmount = totalTaxAmount.add(lineTaxAmount);

            TransactionDetail detail = TransactionDetail.builder()
                    .transaction(savedTransaction)
                    .product(product)
                    .quantity(quantityInt)
                    .priceAtTransaction(priceAtTransaction) // Field di entity Anda 'price'
                    .totalAmount(lineTotalAmount)
                    .build();
            transactionDetails.add(detail);
        }

        List<TransactionDetail> savedDetails = transactionDetailRepository.saveAll(transactionDetails);

        savedTransaction.setNetAmount(totalNetAmount.setScale(2, RoundingMode.HALF_UP));
        savedTransaction.setTaxAmount(totalTaxAmount.setScale(2, RoundingMode.HALF_UP));
        savedTransaction.setTotalAmount(totalNetAmount.add(totalTaxAmount).setScale(2, RoundingMode.HALF_UP));
        savedTransaction.setDetails(savedDetails);

        Transaction finalTransaction = transactionRepository.save(savedTransaction);
        return TransactionMapper.toTransactionResponse(finalTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with ID " + id + " not found."));
        User currentUser = userService.getCurrentUser();
        boolean isCustomer = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleType.CUSTOMER);

        if (isCustomer) {
            UUID customerIdOfCurrentUser = getCustomerIdForUser(currentUser);
            if (customerIdOfCurrentUser == null || !transaction.getCustomer().getId().equals(customerIdOfCurrentUser)) {
                throw new UnauthorizedException("You are not authorized to view this transaction.");
            }
        }
        return TransactionMapper.toTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllFiltered(TransactionFilterRequest request, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        boolean isCustomer = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleType.CUSTOMER);

        // Komentar: Specification Anda tidak bisa filter by PaymentMethod karena field tidak ada
        Specification<Transaction> spec = TransactionSpecification.getSpecification(request);

        if (isCustomer) {
            UUID customerIdOfCurrentUser = getCustomerIdForUser(currentUser);
            if (customerIdOfCurrentUser == null) return Page.empty(pageable);

            Specification<Transaction> customerSpec = (root, query, cb) ->
                    cb.equal(root.get("customer").get("id"), customerIdOfCurrentUser);
            spec = spec.and(customerSpec);
        }

        // Atur sorting berdasarkan request jika ada, jika tidak gunakan default dari pageable
        Pageable finalPageable = pageable;
        if (request.getSortByNewst() != null) {
            Sort.Direction direction = request.getSortByNewst() ? Sort.Direction.DESC : Sort.Direction.ASC;
            // Buat pageable baru dengan sort dari request, tapi page & size dari parameter
            finalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "transactionTime"));
        } else if (pageable.getSort().isUnsorted()) {
            // Default sort jika tidak ada di request dan pageable
            finalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "transactionTime"));
        }


        Page<Transaction> transactionPage = transactionRepository.findAll(spec, finalPageable);
        return transactionPage.map(TransactionMapper::toTransactionResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse updateStatus(UUID id, PaymentStatus status) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with ID " + id + " not found."));

        if (transaction.getPaymentStatus() == PaymentStatus.CANCELLED) {
            throw new BusinessLogicException("Cannot update status for a cancelled transaction.");
        }
        if (status == PaymentStatus.NOT_PAID && transaction.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Cannot change status from PAID back to NOT_PAID.");
        }

        transaction.setPaymentStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toTransactionResponse(updatedTransaction);
    }

    // --- Placeholder Helper ---
    private UUID getCustomerIdForUser(User user) {
        // TODO: Implementasikan logika untuk mendapatkan Customer ID dari User
        // Contoh: Cari Customer berdasarkan user ID jika ada relasi
        System.err.println("WARNING: Logic to get Customer ID for User is required for proper authorization!");
        // return customerRepository.findByUserId(user.getId()).map(Customer::getId).orElse(null);
        return null; // Kembalikan null jika tidak ketemu/belum implementasi
    }
}
