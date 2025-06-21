// src/main/java/com/enigma/live_code_springboot/service/impl/UserServiceImpl.java
package com.belajar.springboot.service.impl;

import com.belajar.springboot.dto.request.UpdateProfileRequest;
import com.belajar.springboot.dto.response.UserResponse;
import com.belajar.springboot.entity.User;
import com.belajar.springboot.exception.BadRequestException;
import com.belajar.springboot.exception.ResourceNotFoundException;
import com.belajar.springboot.exception.UnauthorizedException;
import com.belajar.springboot.mapper.UserMapper;
import com.belajar.springboot.repository.UserRepository;
import com.belajar.springboot.security.UserDetailsImpl;
import com.belajar.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Login failed: User not found with username or email: " + usernameOrEmail));
        return UserDetailsImpl.build(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = findEntityById(id);
        return UserMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(UUID id) {
        User user = findEntityById(id);
        // Tambahkan validasi jika perlu (e.g., admin tidak bisa delete diri sendiri)
        userRepository.delete(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User currentUser = getCurrentUser();

        // Validasi email jika berubah
        if (!currentUser.getEmail().equalsIgnoreCase(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already in use by another account.");
        }

        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());

        if (StringUtils.hasText(request.getPassword())) {
            // Pastikan password baru tidak sama dengan yang lama jika ada aturan
            // if (passwordEncoder.matches(request.getPassword(), currentUser.getPassword())) {
            //     throw new BadRequestException("New password cannot be the same as the old password.");
            // }
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.saveAndFlush(currentUser);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("No authenticated user found. Please login.");
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            // Ambil ulang dari DB
            return userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user data not found in database. ID: " + userDetails.getId()));
        } else {
            throw new UnauthorizedException("Unexpected principal type: " + authentication.getPrincipal().getClass());
        }
    }
}