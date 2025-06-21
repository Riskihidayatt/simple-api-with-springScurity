// src/main/java/com/enigma/live_code_springboot/service/impl/CustomerServiceImpl.java
package com.belajar.springboot.service.impl;

import com.belajar.springboot.dto.request.CustomerRequest;
import com.belajar.springboot.dto.request.UserRegisterRequest;
import com.belajar.springboot.dto.response.CustomerResponse;
import com.belajar.springboot.dto.response.UserResponse;
import com.belajar.springboot.entity.Customer;
import com.belajar.springboot.entity.User;
import com.belajar.springboot.exception.BadRequestException;
import com.belajar.springboot.exception.ResourceNotFoundException;
import com.belajar.springboot.exception.BusinessLogicException;
import com.belajar.springboot.repository.CustomerRepository;
import com.belajar.springboot.repository.TransactionRepository;
import com.belajar.springboot.service.AuthService;
import com.belajar.springboot.service.CustomerService;
import com.belajar.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Import StringUtils

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final AuthService authService; // <-- Inject AuthService
    private final TransactionRepository transactionRepository; // Inject untuk validasi delete
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse create(CustomerRequest request) {
        User creator = userService.getCurrentUser(); // Staff/Admin yang membuat
        LocalDate birthDate = parseBirthDate(request.getBirthdate());
        User customerLoginUser = null; // Akan diisi jika login dibuat

        // Cek apakah data login diberikan di request
        boolean shouldCreateLogin = StringUtils.hasText(request.getUsername())
                && StringUtils.hasText(request.getEmail())
                && StringUtils.hasText(request.getPassword());

        if (shouldCreateLogin) {
            // Validasi password minimal length (bisa juga di DTO dengan groups)
            if (request.getPassword().length() < 8) {
                throw new BadRequestException("Password must be at least 8 characters long when creating login account.");
            }

            // Buat DTO untuk registrasi user customer
            UserRegisterRequest registerDto = UserRegisterRequest.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .fullname(request.getName()) // Gunakan nama customer sebagai fullname user
                    .build();
            try {
                // Panggil AuthService.registerCustomer untuk membuat user dengan role CUSTOMER
                UserResponse userResponse = authService.registerCustomer(registerDto);
                // Dapatkan entity User yang baru dibuat untuk mengambil ID nya
                customerLoginUser = userService.findEntityById(userResponse.getId());
            } catch (BadRequestException e) {
                // Tangani jika username/email sudah ada dari AuthService
                throw new BadRequestException("Failed to create login account for customer: " + e.getMessage());
                // Atau bisa juga tidak throw error dan tetap buat Customer tanpa login? Tergantung kebutuhan.
            }
        }

        // Buat entity Customer
        Customer customer = Customer.builder()
                .name(request.getName())
                .birthDate(birthDate)
                .birthPlace(request.getBirthplace())
                .userId(customerLoginUser != null ? customerLoginUser.getId() : null) // Set userId jika login dibuat
                .createdBy(creator)
                .build(); // ID dan createdAt otomatis

        // Simpan Customer
        Customer savedCustomer = customerRepository.save(customer);

        // Kembalikan response
        return mapToResponse(savedCustomer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse update(UUID id, CustomerRequest request) {
        User updater = userService.getCurrentUser();
        Customer existingCustomer = findEntityById(id);
        LocalDate birthDate = parseBirthDate(request.getBirthdate());

        // Update data customer dasar
        existingCustomer.setName(request.getName());
        existingCustomer.setBirthDate(birthDate);
        existingCustomer.setBirthPlace(request.getBirthplace());
        existingCustomer.setUpdatedBy(updater);
        // Catatan: Endpoint ini TIDAK menghandle pembuatan/update akun login.
        // Jika customer belum punya login dan ingin dibuatkan, atau ingin ganti
        // username/email/password loginnya, perlu mekanisme/endpoint terpisah.
        // Field userId di customer sebaiknya tidak diubah di sini.

        return mapToResponse(customerRepository.save(existingCustomer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(UUID id) {
        Customer customer = findEntityById(id);
        // Validasi: Cek jika customer punya transaksi
        if (transactionRepository.existsByCustomerId(id)) { // Asumsi method ini ada di TransactionRepo
            throw new BusinessLogicException("Cannot delete customer ID " + id + " because they have existing transactions.");
        }
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findEntityById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + id + " not found."));
    }

    // --- Helper Methods ---
    private LocalDate parseBirthDate(String birthDateString) {
        if (birthDateString == null) {
            throw new BadRequestException("Birthdate cannot be null.");
        }
        try {
            return LocalDate.parse(birthDateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid birthdate format. Expected format: yyyy-MM-dd");
        }
    }

    // Helper mapping (sudah ada sebelumnya)
    private CustomerResponse mapToResponse(Customer customer) {
        String createdByUsername = (customer.getCreatedBy() != null) ? customer.getCreatedBy().getUsername() : null;
        String updatedByUsername = (customer.getUpdatedBy() != null) ? customer.getUpdatedBy().getUsername() : null;

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .birthdate(customer.getBirthDate() != null ? customer.getBirthDate().format(DATE_FORMATTER) : null)
                .birthplace(customer.getBirthPlace())
                .createdAt(customer.getCreatedAt())
                .createdBy(createdByUsername)
                .updatedAt(customer.getUpdatedAt()) // Pastikan field entity 'updatedAt'
                .updatedBy(updatedByUsername)
                // Opsional: tambahkan userId jika ingin ditampilkan di response
                // .userId(customer.getUserId())
                .build();
    }
}