// src/main/java/com/enigma/live_code_springboot/config/DataInitializer.java
package com.belajar.springboot.config;

import com.belajar.springboot.constant.PaymentMethod;
import com.belajar.springboot.constant.PaymentStatus;
import com.belajar.springboot.constant.RoleType;
import com.belajar.springboot.dto.request.TransactionDetailRequest; // Import DTO ini
import com.belajar.springboot.entity.*;
import com.belajar.springboot.repository.*;
import com.enigma.live_code_springboot.entity.*;
import com.enigma.live_code_springboot.repository.*;
import com.belajar.springboot.service.AuthService; // <-- Inject AuthService
import com.belajar.springboot.service.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final TaxRepository taxRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository; // Inject ini juga
    private final AuthService authService; // <-- Inject AuthService

    // Password plaintext untuk dummy users
    private final String ADMIN_PASS = "admin1234";
    private final String STAFF_PASS = "staff1234";
    private final String CUSTOMER_PASS = "cus123456";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;


    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void initData() {
        log.info("Starting Data Initialization...");

        // 1. Ensure Roles Exist
        Role adminRole = roleService.getOrCreate(RoleType.ADMIN);
        Role staffRole = roleService.getOrCreate(RoleType.STAFF);
        Role customerRole = roleService.getOrCreate(RoleType.CUSTOMER);
        log.info("Roles ensured.");

        // 2. Create Admin User (if not exists) - Langsung pakai repo + encoder
        User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
            log.info("Creating admin user...");
            User newUser = User.builder()
                    .username("admin")
                    .email("admin@livecode.com")
                    .password(passwordEncoder.encode(ADMIN_PASS))
                    .fullName("Administrator")
                    .roles(Set.of(adminRole))
                    .isEnabled(true).isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                    .build();
            return userRepository.save(newUser);
        });
        log.info("Admin user ensured.");

        // 3. Create Staff Users (if not exists) - Langsung pakai repo + encoder
        User staffUser1 = userRepository.findByUsername("staff01").orElseGet(() -> {
            log.info("Creating staff01 user...");
            User newUser = User.builder()
                    .username("staff01")
                    .email("staff01@livecode.com")
                    .password(passwordEncoder.encode(STAFF_PASS))
                    .fullName("Staff Person One")
                    .roles(Set.of(staffRole))
                    .isEnabled(true).isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                    .build();
            return userRepository.save(newUser);
        });
        User staffUser2 = userRepository.findByUsername("staff02").orElseGet(() -> {
            log.info("Creating staff02 user...");
            User newUser = User.builder()
                    .username("staff02")
                    .email("staff02@livecode.com")
                    .password(passwordEncoder.encode(STAFF_PASS))
                    .fullName("Staff Person Two")
                    .roles(Set.of(staffRole))
                    .isEnabled(true).isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                    .build();
            return userRepository.save(newUser);
        });
        log.info("Staff users ensured.");

        // --- PERUBAHAN CARA MEMBUAT CUSTOMER & USER LOGIN ---
        // 4. Create Customer Budi (with Login)
        Customer custBudi = createCustomerWithLoginIfNotExist(
                "Budi Santoso", "budi.s@mail.com", "budi_santoso", CUSTOMER_PASS,
                "1990-05-15", "Jakarta", staffUser1
        );

        // 5. Create Customer Dewi (with Login)
        Customer custDewi = createCustomerWithLoginIfNotExist(
                "Dewi Lestari", "dewi.l@mail.com", "dewi_lestari", CUSTOMER_PASS,
                "1992-08-22", "Bandung", staffUser1
        );

        // 6. Create Customer Eko (without Login)
        Customer custEko = createCustomerWithoutLoginIfNotExist(
                "Eko Prasetyo", "1988-11-01", "Surabaya", staffUser2
        );

        // 7. Create Customer Fitri (without Login)
        Customer custFitri = createCustomerWithoutLoginIfNotExist(
                "Fitriani", "1995-02-10", "Medan", staffUser2
        );

        // 8. Create Customer Gunawan (without Login)
        Customer custGuna = createCustomerWithoutLoginIfNotExist(
                "Gunawan", "1991-07-07", "Yogyakarta", staffUser1
        );
        log.info("Customers (with/without login) ensured.");
        // --- AKHIR PERUBAHAN ---


        // 9. Create Taxes (if not exists)
        Tax taxPPN = createOrGetTax("PPN", "11.00");
        Tax taxService = createOrGetTax("Service", "5.00");
        Tax taxPB1 = createOrGetTax("PB1", "10.00");
        log.info("Taxes ensured.");

        // 10. Create Products (if not exists - with taxes)
        Product prodNasgor = createOrGetProduct("Nasi Goreng Spesial", "25000.00", Set.of(taxPPN, taxPB1));
        Product prodMie = createOrGetProduct("Mie Goreng Seafood", "28000.00", Set.of(taxPPN, taxPB1));
        Product prodAyam = createOrGetProduct("Ayam Bakar Madu", "35000.00", Set.of(taxPPN, taxPB1, taxService));
        Product prodEsteh = createOrGetProduct("Es Teh Manis", "5000.00", Set.of(taxPB1));
        Product prodKopi = createOrGetProduct("Kopi Hitam", "8000.00", Set.of(taxPB1));
        log.info("Products ensured.");

        // 11. Create Transactions and Details (only if table is empty)
        if (transactionRepository.count() == 0) {
            log.info("Creating dummy transactions...");
            createDummyTransaction(custBudi, staffUser1, LocalDateTime.of(2025, 5, 4, 12, 0, 0), PaymentStatus.PAID, PaymentMethod.CASH, List.of(new TransactionDetailRequest(prodNasgor.getId(), 2)));
            createDummyTransaction(custDewi, staffUser2, LocalDateTime.of(2025, 5, 4, 12, 5, 0), PaymentStatus.PAID, PaymentMethod.E_WALLET, List.of(new TransactionDetailRequest(prodMie.getId(), 1), new TransactionDetailRequest(prodKopi.getId(), 1)));
            createDummyTransaction(custBudi, staffUser1, LocalDateTime.of(2025, 5, 4, 13, 10, 0), PaymentStatus.NOT_PAID, PaymentMethod.TRANSFER, List.of(new TransactionDetailRequest(prodAyam.getId(), 1)));
            createDummyTransaction(custEko, staffUser2, LocalDateTime.of(2025, 5, 4, 14, 15, 0), PaymentStatus.PAID, PaymentMethod.DEBIT_CARD, List.of(new TransactionDetailRequest(prodEsteh.getId(), 1)));
            createDummyTransaction(custDewi, staffUser1, LocalDateTime.of(2025, 5, 4, 15, 20, 0), PaymentStatus.CANCELLED, PaymentMethod.CASH, List.of(new TransactionDetailRequest(prodNasgor.getId(), 1), new TransactionDetailRequest(prodMie.getId(), 1), new TransactionDetailRequest(prodKopi.getId(), 1)));
            log.info("Dummy transactions created.");
        } else {
            log.info("Transactions already exist, skipping dummy transaction creation.");
        }

        log.info("Data Initialization finished successfully.");
    }

    // --- Helper Methods ---

    // Helper untuk user (sama seperti sebelumnya)
    private User createOrGetUser(String username, String email, String rawPassword, String fullName, Role role) {
        return userRepository.findByUsernameOrEmail(username, email).orElseGet(() -> {
            log.info("Creating user: {}", username);
            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .roles(Set.of(role))
                    .isEnabled(true).isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                    .build();
            return userRepository.save(newUser);
        });
    }

    // Helper BARU: Membuat Customer dan User Login sekaligus
    private Customer createCustomerWithLoginIfNotExist(String name, String email, String username, String rawPassword, String birthDateStr, String birthPlace, User createdBy) {
        // Cek dulu apakah user/email sudah ada
        Optional<User> existingUser = userRepository.findByUsernameOrEmail(username, email);
        Optional<Customer> existingCustomer = Optional.empty();
        if (existingUser.isPresent()) {
            // Jika user sudah ada, coba cari customer yang terhubung dengannya
            existingCustomer = customerRepository.findByUserId(existingUser.get().getId());
        } else {
            // Jika user belum ada, coba cari customer berdasarkan nama & tgl lahir (jika ada customer tanpa login sebelumnya)
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            existingCustomer = customerRepository.findAll().stream()
                    .filter(c -> c.getName().equals(name) && c.getBirthDate().equals(birthDate) && c.getUserId() == null)
                    .findFirst();
        }

        if(existingCustomer.isPresent() && existingCustomer.get().getUserId() != null){
            // Customer sudah ada DAN sudah punya user ID terkait, kembalikan saja
            log.info("Customer and linked User already exist for: {}", name);
            return existingCustomer.get();
        } else {
            // Buat User login baru (jika belum ada)
            User customerLoginUser = existingUser.orElseGet(() -> {
                log.info("Creating login user for customer {}: {}", name, username);
                Role customerRole = roleService.getOrCreate(RoleType.CUSTOMER);
                User newUser = User.builder()
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .fullName(name) // Gunakan nama customer
                        .roles(Set.of(customerRole))
                        .isEnabled(true).isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                        .build();
                return userRepository.save(newUser);
            });

            // Buat atau update Customer
            Customer customerToSave;
            if(existingCustomer.isPresent()){
                // Customer sudah ada tapi belum punya link user_id, update saja
                log.info("Linking user {} to existing customer {}", username, name);
                customerToSave = existingCustomer.get();
                customerToSave.setUserId(customerLoginUser.getId());
            } else {
                // Buat Customer baru
                log.info("Creating new customer and linking user {}: {}", username, name);
                customerToSave = Customer.builder()
                        .name(name)
                        .birthDate(LocalDate.parse(birthDateStr))
                        .birthPlace(birthPlace)
                        .userId(customerLoginUser.getId()) // Link ke user login
                        .createdBy(createdBy)
                        .build();
            }
            return customerRepository.save(customerToSave);
        }
    }

    // Helper BARU: Membuat Customer TANPA User Login (jika belum ada)
    private Customer createCustomerWithoutLoginIfNotExist(String name, String birthDateStr, String birthPlace, User createdBy) {
        LocalDate birthDate = LocalDate.parse(birthDateStr);
        // Cari berdasarkan nama & tgl lahir & user_id null
        return customerRepository.findAll().stream()
                .filter(c -> c.getName().equals(name) && c.getBirthDate().equals(birthDate) && c.getUserId() == null)
                .findFirst()
                .orElseGet(() -> {
                    log.info("Creating customer without login: {}", name);
                    Customer newCustomer = Customer.builder()
                            .name(name)
                            .birthDate(birthDate)
                            .birthPlace(birthPlace)
                            .userId(null) // Tidak ada user login
                            .createdBy(createdBy)
                            .build();
                    return customerRepository.save(newCustomer);
                });
    }


    // Helper createOrGetTax (sama seperti sebelumnya)
    private Tax createOrGetTax(String name, String percentageStr) {
        BigDecimal percentage = new BigDecimal(percentageStr);
        return taxRepository.findAll().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Creating tax: {}", name);
                    Tax newTax = Tax.builder().name(name).percentage(percentage).build();
                    return taxRepository.save(newTax);
                });
    }

    // Helper createOrGetProduct (sama seperti sebelumnya)
    private Product createOrGetProduct(String name, String priceStr, Set<Tax> taxes) {
        BigDecimal price = new BigDecimal(priceStr);
        return productRepository.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Creating product: {}", name);
                    Product newProduct = Product.builder().name(name).price(price).taxes(taxes).build();
                    return productRepository.save(newProduct);
                });
    }

    // Helper createDummyTransaction (sama seperti sebelumnya)
    private void createDummyTransaction(Customer customer, User createdBy, LocalDateTime time, PaymentStatus status, PaymentMethod method, List<TransactionDetailRequest> detailsDto) {
        log.info("Creating dummy transaction for customer: {}", customer.getName());
        BigDecimal totalNetAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        Transaction transaction = Transaction.builder()
                .customer(customer)
                .createdBy(createdBy)
                .transactionTime(time)
                .paymentStatus(status)
                .paymentMethod(method)
                .build();
        Transaction savedTransaction = transactionRepository.saveAndFlush(transaction);

        List<TransactionDetail> detailEntities = new ArrayList<>();
        for (TransactionDetailRequest detailDto : detailsDto) {
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(); // Uups, handle not found better
            int quantityInt = detailDto.getQuantity();
            BigDecimal quantity = new BigDecimal(quantityInt);
            BigDecimal priceAtTransaction = product.getPrice();
            BigDecimal lineNetAmount = priceAtTransaction.multiply(quantity);
            BigDecimal lineTaxAmount = BigDecimal.ZERO;
            if (product.getTaxes() != null) {
                for (Tax tax : product.getTaxes()) {
                    if (tax.getPercentage() != null) {
                        BigDecimal taxRate = tax.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        lineTaxAmount = lineTaxAmount.add(lineNetAmount.multiply(taxRate));
                    }
                }
            }
            BigDecimal lineTotalAmount = lineNetAmount.add(lineTaxAmount);
            totalNetAmount = totalNetAmount.add(lineNetAmount);
            totalTaxAmount = totalTaxAmount.add(lineTaxAmount);
            TransactionDetail detail = TransactionDetail.builder()
                    .transaction(savedTransaction)
                    .product(product)
                    .quantity(quantityInt)
                    .priceAtTransaction(priceAtTransaction) // field Anda 'price'
                    .totalAmount(lineTotalAmount)
                    .build();
            detailEntities.add(detail);
        }

        transactionDetailRepository.saveAll(detailEntities); // Simpan detail

        savedTransaction.setNetAmount(totalNetAmount.setScale(2, RoundingMode.HALF_UP));
        savedTransaction.setTaxAmount(totalTaxAmount.setScale(2, RoundingMode.HALF_UP));
        savedTransaction.setTotalAmount(totalNetAmount.add(totalTaxAmount).setScale(2, RoundingMode.HALF_UP));
        savedTransaction.setDetails(detailEntities); // Set relasi

        transactionRepository.save(savedTransaction); // Update transaksi utama
    }
}